package org.clapper.avsl

import org.clapper.avsl.handler.{Handler, NullHandler}
import org.clapper.avsl.formatter.{Formatter, NullFormatter}
import org.clapper.avsl.config._

import java.util.Date

import scala.collection.mutable.{Map => MutableMap, Set => MutableSet}
import scala.annotation.tailrec
import scala.io.Source

abstract sealed class LogLevel(val label: String, val value: Int) {
  override def toString = label
}

/**
  * Utility methods for log levels.
  */
object LogLevel {
  case object All extends LogLevel("All", 0)
  case object Trace extends LogLevel("TRACE", 10)
  case object Debug extends LogLevel("DEBUG", 20)
  case object Info extends LogLevel("INFO", 30)
  case object Warn extends LogLevel("WARN", 40)
  case object Error extends LogLevel("ERROR", 50)
  case object NoLogging extends LogLevel("NoLogging", 100)

  private val Levels = List(All, Trace, Debug, Info, Warn, Error, NoLogging)

  def fromString(string: String): Option[LogLevel] = {
    val s = string.toLowerCase

    @tailrec def find(levelsLeft: List[LogLevel]): Option[LogLevel] = {
      levelsLeft match {
        case level :: Nil if (level.label.toLowerCase == s) =>
          Some(level)
        case level :: Nil =>
          None
        case level :: tail if (level.label.toLowerCase == s) =>
          Some(level)
        case level :: tail =>
          find(tail)
        case _ =>
          None
      }
    }

    find(Levels)
  }
}

/**
  * All the pieces of a message, consolidated in one place. This is how messages
  * are passed to formatters and handlers.
  *
  * @param name       the name (usually, the class name) of the logger issuing
  *                   the message
  * @param date       the date of the message, as milliseconds from the epoch
  * @param level      the log level of the message
  * @param text       the text of the message
  * @param exception  an optional exception
  */
case class LogMessage(name: String,
                      date: Long,
                      level: LogLevel,
                      message: AnyRef,
                      exception: Option[Throwable])

/**
  * Basic trait for all logger implementations.
  */
trait Logger {
  val name: String
  val level: LogLevel

  /** Determine whether trace logging is enabled.
    */
  def isTraceEnabled: Boolean

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def trace(msg: => AnyRef): Unit

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def trace(msg: => AnyRef, t: => Throwable): Unit

  /** Determine whether debug logging is enabled.
    */
  def isDebugEnabled: Boolean

  /** Issue a debug logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def debug(msg: => AnyRef): Unit

  /** Issue a debug logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def debug(msg: => AnyRef, t: => Throwable): Unit

  /** Determine whether trace logging is enabled.
    */
  def isErrorEnabled: Boolean

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def error(msg: => AnyRef): Unit

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def error(msg: => AnyRef, t: => Throwable): Unit

  /** Determine whether trace logging is enabled.
    */
  def isInfoEnabled: Boolean

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def info(msg: => AnyRef): Unit

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def info(msg: => AnyRef, t: => Throwable): Unit

  /** Determine whether trace logging is enabled.
    */
  def isWarnEnabled: Boolean

  /** Issue a warning logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def warn(msg: => AnyRef): Unit

  /** Issue a warning logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def warn(msg: => AnyRef, t: => Throwable): Unit

  /** Determine whether a specific logging level is enabled.
    *
    * @param logLevel the level
    */
  def isLevelEnabled(logLevel: LogLevel): Boolean

  /** Log a message at an arbitrary level.
    *
    * @param logLevel  the level
    * @param msg       the message to log
    */
  def log(logLevel: LogLevel, msg: => AnyRef): Unit

  /** Get the loggers list of handlers.
    *
    * @return the list of handlers
    */
  def handlers: List[Handler]
}

/**
  * The basic logger class. This class provides its own Scala-friendly
  * methods, as well as non-marker SLF4J methods.
  */
class StandardLogger private[avsl] (val name: String,
                                    val level: LogLevel,
                                    val handlers: List[Handler])
extends Logger {
  /** Determine whether trace logging is enabled.
    */
  def isTraceEnabled = level.value <= LogLevel.Trace.value

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def trace(msg: => AnyRef): Unit =
    dispatch(isTraceEnabled, LogLevel.Trace, msg)

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def trace(msg: => AnyRef, t: => Throwable): Unit =
    dispatch(isTraceEnabled, LogLevel.Trace, msg, t)

  /** Determine whether debug logging is enabled.
    */
  def isDebugEnabled = level.value <= LogLevel.Debug.value

  /** Issue a debug logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def debug(msg: => AnyRef): Unit =
    dispatch(isDebugEnabled, LogLevel.Debug, msg)

  /** Issue a debug logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def debug(msg: => AnyRef, t: => Throwable): Unit =
    dispatch(isDebugEnabled, LogLevel.Debug, msg, t)

  /** Determine whether trace logging is enabled.
    */
  def isErrorEnabled = level.value <= LogLevel.Error.value

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def error(msg: => AnyRef): Unit =
    dispatch(isErrorEnabled, LogLevel.Error, msg)

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def error(msg: => AnyRef, t: => Throwable): Unit =
    dispatch(isErrorEnabled, LogLevel.Error, msg, t)

  /** Determine whether trace logging is enabled.
    */
  def isInfoEnabled = level.value <= LogLevel.Info.value

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def info(msg: => AnyRef): Unit =
    dispatch(isInfoEnabled, LogLevel.Info, msg)

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def info(msg: => AnyRef, t: => Throwable): Unit =
    dispatch(isInfoEnabled, LogLevel.Info, msg, t)

  /** Determine whether trace logging is enabled.
    */
  def isWarnEnabled = level.value <= LogLevel.Warn.value

  /** Issue a warning logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def warn(msg: => AnyRef): Unit =
    dispatch(isWarnEnabled, LogLevel.Warn, msg)

  /** Issue a warning logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def warn(msg: => AnyRef, t: => Throwable): Unit =
    dispatch(isWarnEnabled, LogLevel.Warn, msg, t)

  /** Determine whether a specific logging level is enabled.
    *
    * @param logLevel the level
    */
  def isLevelEnabled(logLevel: LogLevel) = level.value <= logLevel.value

  /** Log a message at an arbitrary level.
    *
    * @param logLevel  the level
    * @param msg       the message to log
    */
  def log(logLevel: LogLevel, msg: => AnyRef): Unit =
    dispatch(isLevelEnabled(logLevel), logLevel, msg)

  protected def forceLog(logLevel: LogLevel, msg: => AnyRef): Unit =
    dispatch(true, logLevel, msg)

  protected def forceLog(logLevel: LogLevel,
                         msg: => AnyRef,
                         t: Throwable): Unit = {
    dispatch(true, logLevel, msg, t)
  }

  private def dispatch(ok: => Boolean,
                       logLevel: LogLevel,
                       msg: => AnyRef): Unit = {
    if (ok)
      dispatchToHandlers(LogMessage(name, System.currentTimeMillis,
                                    logLevel, msg, None))
  }

  private def dispatch(ok: => Boolean,
                       logLevel: LogLevel,
                       msg: => AnyRef,
                       t: Throwable): Unit = {
    if (ok)
      dispatchToHandlers(LogMessage(name, System.currentTimeMillis,
                                    logLevel, msg, Some(t)))
  }

  private def dispatchToHandlers(message: LogMessage) = {
    for (h <- handlers; if (h.level.value <= message.level.value))
      h.synchronized { h.log(h.formatter.format(message), message) }
  }
}

/**
  * A null logger.
  */
class NullLogger private[avsl](val name: String, val level: LogLevel)
extends Logger {
  val handlers: List[Handler] = Nil

  /** Determine whether trace logging is enabled.
    */
  def isTraceEnabled = false

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def trace(msg: => AnyRef) = {}

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def trace(msg: => AnyRef, t: => Throwable) = {}

  /** Determine whether debug logging is enabled.
    */
  def isDebugEnabled = false

  /** Issue a debug logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def debug(msg: => AnyRef) = {}

  /** Issue a debug logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def debug(msg: => AnyRef, t: => Throwable) = {}

  /** Determine whether trace logging is enabled.
    */
  def isErrorEnabled = false

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def error(msg: => AnyRef) = {}

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def error(msg: => AnyRef, t: => Throwable) = {}

  /** Determine whether trace logging is enabled.
    */
  def isInfoEnabled = false

  /** Issue a trace logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def info(msg: => AnyRef) = {}

  /** Issue a trace logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def info(msg: => AnyRef, t: => Throwable) = {}

  /** Determine whether trace logging is enabled.
    */
  def isWarnEnabled = false

  /** Issue a warning logging message.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    */
  def warn(msg: => AnyRef) = {}

  /** Issue a warning logging message, with an exception.
    *
    * @param msg  the message object. `toString()` is called to convert it
    *             to a loggable string.
    * @param t    the exception to include with the logged message.
    */
  def warn(msg: => AnyRef, t: => Throwable) = {}

  /** Determine whether a specific logging level is enabled.
    *
    * @param logLevel the level
    */
  def isLevelEnabled(logLevel: LogLevel) = false

  /** Log a message at an arbitrary level.
    *
    * @param logLevel  the level
    * @param msg       the message to log
    */
  def log(logLevel: LogLevel, msg: => AnyRef) = {}
}

/**
  * Logging factory. Generally, you don't need to access this class; the
  * default factory is automatically used by the methods in the `Logger`
  * object. However, for testing, it can be useful to generate a separate
  * factory.
  *
  * @param configSource the source from which to configure the factory, or
  *                     None for the default
  */
class LoggerFactory(configSource: Option[Source]) {
  private val handlers = MutableMap.empty[String, Handler]
  private val loggers = MutableMap.empty[String, Logger]
  private val formatters = MutableMap.empty[String, Formatter]

  val config = configSource.map { c => Some(AVSLConfiguration(c)) }.
                            getOrElse(AVSLConfiguration())


  lazy val rootLogger = logger(Logger.RootLoggerName)

  /** Get the logger for the specified class.
    *
    * @param cls  the class
    *
    * @return the logger
    */
  def logger(cls: Class[_]): Logger = logger(cls.getName)

  /** Get the named logger.
    *
    * @param name  the logger name
    *
    * @return the logger
    */
  def logger(name: String): Logger = {
    def newLogger(logConfig: LoggerConfig): Logger = {
      val handlers = getHandlers(logConfig.handlerNames)
      val logger = new StandardLogger(name, logConfig.level, handlers)
      loggers += (name -> logger)
      logger
    }

    def findLogger(name: String): Logger = {
      config.map { c => newLogger(c.loggerConfigFor(name)) }.
             getOrElse(new NullLogger(name, LogLevel.NoLogging))
    }

    loggers.synchronized {
      loggers.get(name).getOrElse(findLogger(name))
    }
  }

  private def getFormatter(name: String): Formatter = {
    def newFormatter(config: FormatterConfig) = {
      val cls = config.formatterClass
      val ctor = cls.getConstructor(classOf[ConfiguredArguments])
      val formatter = ctor.newInstance(config.args).asInstanceOf[Formatter]
      formatters += (name -> formatter)
      formatter
    }

    def findFormatter = {
      config.map { cfg => newFormatter(cfg.formatters(name)) }.
             getOrElse(new NullFormatter)
    }

    formatters.synchronized {
      formatters.get(name).getOrElse(findFormatter)
    }
  }

  private def newHandler(config: HandlerConfig): Handler = {
    val cls = config.handlerClass
    val ctor = cls.getConstructor(classOf[ConfiguredArguments],
                                  classOf[Formatter],
                                  classOf[LogLevel])
    val formatter = getFormatter(config.formatterName)
    ctor.newInstance(config.args, formatter, config.level).asInstanceOf[Handler]
  }

  private def getHandlers(names: List[String]): List[Handler] = {
    def nameToHandler(name: String) = {
      handlers.get(name).getOrElse {
        config.map { c => newHandler(c.handlers(name)) }.
               getOrElse(new NullHandler(LogLevel.NoLogging, new NullFormatter))
      }
    }

    handlers.synchronized {
      names.map(nameToHandler(_))
    }
  }
}

/**
  * `Logger` companion object.
  */
object Logger {
  val RootLoggerName = "root"

  val DefaultFactory = new LoggerFactory(None)

  /** Get the logger for the specified class.
    *
    * @param cls  the class
    *
    * @return the logger
    */
  def apply(cls: Class[_]): Logger = DefaultFactory.logger(cls)

  /** Get the named logger.
    *
    * @param name  the logger name
    *
    * @return the logger
    */
  def apply(name: String): Logger = DefaultFactory.logger(name)
}
