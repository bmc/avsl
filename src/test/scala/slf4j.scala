import org.scalatest.{Matchers, FlatSpec}
import org.slf4j._

import org.clapper.avsl.{LogLevel, LogMessage, LoggerFactory}
import org.clapper.avsl.slf4j._
import org.clapper.avsl.config.ConfiguredArguments
import org.clapper.avsl.formatter.Formatter
import org.clapper.avsl.handler.Handler

import scala.io.Source

class InMemoryHandler(args: ConfiguredArguments,
                      val formatter: Formatter,
                      val level: LogLevel)
extends Handler {
    import scala.collection.mutable.ArrayBuffer

    val buf = new ArrayBuffer[String]

    def log(message: String, logMessage: LogMessage) = buf += message

    def clear = buf.clear
}

/**
 * Tests the SLF4J interface.
 */
class SLF4JTest extends FlatSpec with Matchers {
  "SLF4J API" should "format messages correctly" in {

    val configString = """
    |[logger_root]
    |level: trace
    |handlers: h1
    |
    |[handler_h1]
    |level: trace
    |class: InMemoryHandler
    |formatter: f1
    |
    |[formatter_f1]
    |class: DefaultFormatter
    |format: (%l) %N %t
    """.stripMargin

    val LoggerName = "org.clapper.avsl"
    val source = Source.fromString(configString)
    val loggerFactory = new LoggerFactory(Some(source))
    val slf4jFactory = new AVSL_SLF4J_LoggerFactory(loggerFactory)
    val slf4jLogger = slf4jFactory.getLogger(LoggerName)
    val realLogger = slf4jLogger.realLogger
    val handlers = realLogger.handlers

    assert(handlers.length == 1)

    val inMemoryHandler = handlers(0) match {
      case handler: InMemoryHandler =>
        handler
      case handler: Any =>
        fail("Expected to find an InMemoryHandler. Got: " +
             handler.getClass.getName)
    }

    def test(expected: String)(logAction: Logger => Unit): Unit = {
      inMemoryHandler.clear

      logAction(slf4jLogger)

      inMemoryHandler.buf.length should equal (1)
      inMemoryHandler.buf(0) should equal (expected)
    }

    val data: List[(String, LogLevel, String)] = List(
      ("test", LogLevel.Info, "(INFO) " + LoggerName + " test"),
      ("foo bar", LogLevel.Error, "(ERROR) " + LoggerName + " foo bar"),
      ("baz", LogLevel.Debug, "(DEBUG) " + LoggerName + " baz")
    )

    for ((message, level, result) <- data) {
      test(result) { slf4jLogger =>
        level match {
          case LogLevel.Error => slf4jLogger.error(message)
          case LogLevel.Warn  => slf4jLogger.warn(message)
          case LogLevel.Info  => slf4jLogger.info(message)
          case LogLevel.Debug => slf4jLogger.debug(message)
          case LogLevel.Trace => slf4jLogger.trace(message)
          case _              => fail("Unknown level: " + level)
        }
      }
    }

    test("(ERROR) " + LoggerName + " args: (none)")         { _.error("args: (none)") }
    test("(ERROR) " + LoggerName + " args: arg1")           { _.error("args: {}", "arg1") }
    test("(ERROR) " + LoggerName + " args: arg1 arg2")      { _.error("args: {} {}", Array("arg1", "arg2"): _*) }
    test("(ERROR) " + LoggerName + " args: arg1 arg2 arg3") { _.error("args: {} {} {}", "arg1", "arg2", "arg3") }
    test("(WARN) "  + LoggerName + " args: (none)")         { _.warn ("args: (none)") }
    test("(WARN) "  + LoggerName + " args: arg1")           { _.warn ("args: {}", "arg1") }
    test("(WARN) "  + LoggerName + " args: arg1 arg2")      { _.warn ("args: {} {}", Array("arg1", "arg2"): _*) }
    test("(WARN) "  + LoggerName + " args: arg1 arg2 arg3") { _.warn ("args: {} {} {}", "arg1", "arg2", "arg3") }
    test("(INFO) "  + LoggerName + " args: (none)")         { _.info ("args: (none)") }
    test("(INFO) "  + LoggerName + " args: arg1")           { _.info ("args: {}", "arg1") }
    test("(INFO) "  + LoggerName + " args: arg1 arg2")      { _.info ("args: {} {}", Array("arg1", "arg2"): _*) }
    test("(INFO) "  + LoggerName + " args: arg1 arg2 arg3") { _.info ("args: {} {} {}", "arg1", "arg2", "arg3") }
    test("(DEBUG) " + LoggerName + " args: (none)")         { _.debug("args: (none)") }
    test("(DEBUG) " + LoggerName + " args: arg1")           { _.debug("args: {}", "arg1") }
    test("(DEBUG) " + LoggerName + " args: arg1 arg2")      { _.debug("args: {} {}", Array("arg1", "arg2"): _*) }
    test("(DEBUG) " + LoggerName + " args: arg1 arg2 arg3") { _.debug("args: {} {} {}", "arg1", "arg2", "arg3") }
    test("(TRACE) " + LoggerName + " args: (none)")         { _.trace("args: (none)") }
    test("(TRACE) " + LoggerName + " args: arg1")           { _.trace("args: {}", "arg1") }
    test("(TRACE) " + LoggerName + " args: arg1 arg2")      { _.trace("args: {} {}", Array("arg1", "arg2"): _*) }
    test("(TRACE) " + LoggerName + " args: arg1 arg2 arg3") { _.trace("args: {} {} {}", "arg1", "arg2", "arg3") }
  }
}
