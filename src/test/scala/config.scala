import org.scalatest.{Matchers, FlatSpec}
import org.clapper.avsl._
import org.clapper.avsl.config._
import org.clapper.avsl.handler.NullHandler
import scala.io.Source

/**
 * Tests the configuration class.
 */
class ConfigTest extends FlatSpec with Matchers {

  private def loadConfig(s: String) =
    new AVSLConfiguration(Source.fromString(s))

  "Configuration" should "load a valid configuration" in {
    val config =
      """
        |[logger_root]
        |level: info
        |handlers: h1
        |[handler_h1]
        |level: info
        |class: NullHandler
        |formatter: f1
        |[formatter_f1]
        |class: NullFormatter
      """.stripMargin

    loadConfig(config)
  }

  it should "abort if a logger section name is bad" in {
    val config =
      """
        |[logger_]
        |level: info
      """.stripMargin

    an [AVSLConfigSectionException] should be thrownBy { loadConfig(config) }
  }

  it should "abort if a handler section name is bad" in {
    val config =
      """
        |[logger_root]
        |level: info
        |[handler_]
      """.stripMargin

    an [AVSLConfigSectionException] should be thrownBy { loadConfig(config) }
  }

  it should "abort if a formatter section name is bad" in {
    val config =
      """
        |[logger_root]
        |level: info
        |[handler_h1]
        |level: trace
        |class: ConsoleHandler
        |formatter: f1
        |[formatter_]
        |class: DefaultFormatter
        |format: [%Y/%M/%d %h:%m:%s:%S] (%l) %t
      """.stripMargin

    an [AVSLConfigSectionException] should be thrownBy { loadConfig(config) }
  }

  it should "abort if a handler is missing a formatter" in {
    val config =
      """
        |[handler_h1]
        |level: trace
        |class: NullHandler
      """.stripMargin

    an [AVSLConfigSectionException] should be thrownBy { loadConfig(config) }
  }

  it should "abort if a handler specifies a bad formatter" in {
    val config =
      """
        |[handler_h1]
        |level: trace
        |class: NullHandler
        |formatter: foo
      """.stripMargin

    an [AVSLConfigException] should be thrownBy { loadConfig(config) }
  }

  it should "abort if a logger specifies to a bad handler" in {
    val config =
      """
        |[logger_foo]
        |pattern: org.clapper.avsl
        |level: info
        |handlers: h1, h2
      """.stripMargin

    an [AVSLConfigException] should be thrownBy { loadConfig(config) }
  }

  it should "abort if a logger has no pattern" in {
    val config =
      """
        |[logger_foo]
        |level: info
        |handlers: h1, h2
      """.stripMargin

    an [AVSLConfigSectionException] should be thrownBy { loadConfig(config) }
  }

  it should "parse a logger section properly" in {
    val config =
      """
        |[logger_foo]
        |level: info
        |handlers: h1, h2
        |pattern: org.clapper.foo
        |[handler_h1]
        |level: trace
        |class: ConsoleHandler
        |formatter: f1
        |[handler_h2]
        |level: trace
        |class: ConsoleHandler
        |formatter: f1
        |[formatter_f1]
        |class: DefaultFormatter
        |format: [%Y/%M/%d %h:%m:%s:%S] (%l) %t
      """.stripMargin

    val cfg = loadConfig(config)
    val loggerConfig = cfg.loggerConfigFor("org.clapper.foo")
    assert(loggerConfig != null)
    loggerConfig.name should equal ("foo")
    loggerConfig.pattern should equal ("org.clapper.foo")
    loggerConfig.handlerNames should equal (List("h1", "h2"))
    loggerConfig.level should equal (LogLevel.Info)
  }

  it should "parse a handler section properly" in {
    val config =
      """
        |[handler_h1]
        |level: trace
        |class: NullHandler
        |formatter: f1
        |[formatter_f1]
        |class: DefaultFormatter
        |format: [%Y/%M/%d %h:%m:%s:%S] (%l) %t
      """.stripMargin

    val cfg = loadConfig(config)
    val handlerConfig = cfg.handlers("h1")
    assert(handlerConfig != null)
    handlerConfig.handlerClass should equal (classOf[NullHandler])
    handlerConfig.formatterName should equal ("f1")
    handlerConfig.level should equal (LogLevel.Trace)
  }
}
