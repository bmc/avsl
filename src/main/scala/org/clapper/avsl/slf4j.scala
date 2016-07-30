/**
  * AVSL SLF4J logging compatibility.
  */
package org.clapper.avsl.slf4j

import org.clapper.avsl._

import org.slf4j.{Logger => SLF4JLogger}
import org.slf4j.Marker;
import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.{MessageFormatter => Formatter}
import org.slf4j.helpers.MarkerIgnoringBase;

/** A trait that, mixed in, provides SLF4J methods for an AVSL Logger class.
  * Kept separate, for clarity. `Marker` methods ignore the marker parameters.
  */
class AVSL_SLF4J_Logger(logger: Logger) extends MarkerIgnoringBase {
  override def getName(): String = logger.name

  def isDebugEnabled(): Boolean = logger.isDebugEnabled

  def debug(fmt: String, arg: Object): Unit =
    if (isDebugEnabled) logger.debug(Formatter.format(fmt, arg).getMessage())

  def debug(fmt: String, args: Object*): Unit = {
    if (isDebugEnabled)
      logger.debug(Formatter.arrayFormat(fmt, args.toArray).getMessage())
  }

  def debug(fmt: String, arg1: Object, arg2: Object): Unit = {
    if (isDebugEnabled)
      logger.debug(Formatter.format(fmt, arg1, arg2).getMessage())
  }

  def debug(msg: String): Unit = logger.debug(msg)

  def debug(msg: String, t: Throwable): Unit = logger.debug(msg, t)

  def isErrorEnabled(): Boolean = logger.isErrorEnabled

  def error(fmt: String, arg: Object): Unit =
    if (isErrorEnabled) logger.error(Formatter.format(fmt, arg).getMessage())

  def error(fmt: String, args: Object*): Unit = {
    if (isErrorEnabled)
      logger.error(Formatter.arrayFormat(fmt, args.toArray).getMessage())
  }

  def error(fmt: String, arg1: Object, arg2: Object): Unit = {
    if (isErrorEnabled)
      logger.error(Formatter.format(fmt, arg1, arg2).getMessage())
  }

  def error(msg: String): Unit = logger.error(msg)

  def error(msg: String, t: Throwable): Unit = logger.error(msg, t)

  def isInfoEnabled(): Boolean = logger.isInfoEnabled

  def info(fmt: String, arg: Object): Unit =
    if (isInfoEnabled) logger.info(Formatter.format(fmt, arg).getMessage())

  def info(fmt: String, args: Object*): Unit = {
    if (isInfoEnabled)
      logger.info(Formatter.arrayFormat(fmt, args.toArray).getMessage())
  }

  def info(fmt: String, arg1: Object, arg2: Object): Unit =
    if (isInfoEnabled) logger.info(Formatter.format(fmt, arg1, arg2).getMessage())

  def info(msg: String): Unit = logger.info(msg)

  def info(msg: String, t: Throwable): Unit = logger.info(msg, t)

  def isTraceEnabled(): Boolean = logger.isTraceEnabled

  def trace(fmt: String, arg: Object): Unit =
    if (isTraceEnabled) logger.trace(Formatter.format(fmt, arg).getMessage())

  def trace(fmt: String, args: Object*): Unit = {
    if (isTraceEnabled)
      logger.trace(Formatter.arrayFormat(fmt, args.toArray).getMessage())
  }

  def trace(fmt: String, arg1: Object, arg2: Object): Unit =
    if (isTraceEnabled) logger.trace(Formatter.format(fmt, arg1, arg2).getMessage())

  def trace(msg: String): Unit = logger.trace(msg)

  def trace(msg: String, t: Throwable): Unit = logger.trace(msg, t)

  def isWarnEnabled(): Boolean = logger.isWarnEnabled

  def warn(fmt: String, arg: Object): Unit =
    if (isWarnEnabled) logger.warn(Formatter.format(fmt, arg).getMessage())

  def warn(fmt: String, args: Object*): Unit = {
    if (isWarnEnabled)
      logger.warn(Formatter.arrayFormat(fmt, args.toArray).getMessage())
  }

  def warn(fmt: String, arg1: Object, arg2: Object): Unit =
    if (isWarnEnabled) logger.warn(Formatter.format(fmt, arg1, arg2).getMessage())

  def warn(msg: String): Unit = logger.warn(msg)

  def warn(msg: String, t: Throwable): Unit = logger.warn(msg, t)

  def realLogger = logger
}

/** SLF4J logger factory.
  */
class AVSL_SLF4J_LoggerFactory(factory: LoggerFactory) extends ILoggerFactory {
  def this() = this(Logger.DefaultFactory)

  def getLogger(name: String): AVSL_SLF4J_Logger =
    new AVSL_SLF4J_Logger(factory.logger(name))
}
