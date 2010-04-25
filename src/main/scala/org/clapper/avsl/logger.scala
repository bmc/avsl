/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2010, Brian M. Clapper
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

  * Neither the names "clapper.org", "AVSL", nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  ---------------------------------------------------------------------------
*/

/**
 * AVSL logging classes.
 */
package org.clapper.avsl

import org.clapper.avsl.handler.{Handler, NullHandler}
import org.clapper.avsl.formatter.{Formatter, NullFormatter}
import org.clapper.avsl.config._

import java.util.Date
import scala.collection.mutable.{Map => MutableMap, Set => MutableSet}
import scala.annotation.tailrec

abstract sealed class LogLevel(val label: String, val value: Int)
{
    override def toString = label
}

case object All extends LogLevel("All", 0)
case object Trace extends LogLevel("TRACE", 10)
case object Debug extends LogLevel("DEBUG", 20)
case object Info extends LogLevel("INFO", 30)
case object Warn extends LogLevel("WARN", 40)
case object Error extends LogLevel("ERROR", 50)
case object NoLogging extends LogLevel("NoLogging", 100)

/**
 * Utility methods for log levels.
 */
object LogLevel
{
    private val Levels = List(All, Trace, Debug, Info, Warn, Error, NoLogging)

    def fromString(string: String): Option[LogLevel] =
    {
        val s = string.toLowerCase

        @tailrec def find(levelsLeft: List[LogLevel]): Option[LogLevel] =
        {
            levelsLeft match
            {
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
trait Logger
{
    val name: String
    val level: LogLevel

    /**
     * Determine whether trace logging is enabled.
     */
    def isTraceEnabled: Boolean

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def trace(msg: => AnyRef): Unit

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def trace(msg: => AnyRef, t: => Throwable): Unit

    /**
     * Determine whether debug logging is enabled.
     */
    def isDebugEnabled: Boolean

    /**
     * Issue a debug logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def debug(msg: => AnyRef): Unit

    /**
     * Issue a debug logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def debug(msg: => AnyRef, t: => Throwable): Unit

    /**
     * Determine whether trace logging is enabled.
     */
    def isErrorEnabled: Boolean

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def error(msg: => AnyRef): Unit

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def error(msg: => AnyRef, t: => Throwable): Unit

    /**
     * Determine whether trace logging is enabled.
     */
    def isInfoEnabled: Boolean

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def info(msg: => AnyRef): Unit

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def info(msg: => AnyRef, t: => Throwable): Unit

    /**
     * Determine whether trace logging is enabled.
     */
    def isWarnEnabled: Boolean

    /**
     * Issue a warning logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def warn(msg: => AnyRef): Unit

    /**
     * Issue a warning logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def warn(msg: => AnyRef, t: => Throwable): Unit

    /**
     * Determine whether a specific logging level is enabled.
     *
     * @param logLevel the level
     */
    def isLevelEnabled(logLevel: LogLevel): Boolean

    /**
     * Log a message at an arbitrary level.
     *
     * @param logLevel  the level
     * @param msg       the message to log
     */
    def log(logLevel: LogLevel, msg: => AnyRef): Unit
}

/**
 * The basic logger class. This class provides its own Scala-friendly
 * methods, as well as non-marker SLF4J methods.
 */
class StandardLogger private[avsl] (val name: String,
                                    val level: LogLevel,
                                    val handlers: List[Handler])
extends Logger
{
    /**
     * Determine whether trace logging is enabled.
     */
    def isTraceEnabled = level.value <= Trace.value

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def trace(msg: => AnyRef): Unit =
        dispatch(isTraceEnabled, Trace, msg)

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def trace(msg: => AnyRef, t: => Throwable): Unit =
        dispatch(isTraceEnabled, Trace, msg, t)

    /**
     * Determine whether debug logging is enabled.
     */
    def isDebugEnabled = level.value <= Debug.value

    /**
     * Issue a debug logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def debug(msg: => AnyRef): Unit =
        dispatch(isDebugEnabled, Debug, msg)

    /**
     * Issue a debug logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def debug(msg: => AnyRef, t: => Throwable): Unit =
        dispatch(isDebugEnabled, Debug, msg, t)

    /**
     * Determine whether trace logging is enabled.
     */
    def isErrorEnabled = level.value <= Error.value

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def error(msg: => AnyRef): Unit = dispatch(isErrorEnabled, Error, msg)

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def error(msg: => AnyRef, t: => Throwable): Unit =
        dispatch(isErrorEnabled, Error, msg, t)

    /**
     * Determine whether trace logging is enabled.
     */
    def isInfoEnabled = level.value <= Info.value

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def info(msg: => AnyRef): Unit =
        dispatch(isInfoEnabled, Info, msg)

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def info(msg: => AnyRef, t: => Throwable): Unit =
        dispatch(isInfoEnabled, Info, msg, t)

    /**
     * Determine whether trace logging is enabled.
     */
    def isWarnEnabled = level.value <= Warn.value

    /**
     * Issue a warning logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def warn(msg: => AnyRef): Unit =
        dispatch(isWarnEnabled, Warn, msg)

    /**
     * Issue a warning logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def warn(msg: => AnyRef, t: => Throwable): Unit =
        dispatch(isWarnEnabled, Warn, msg, t)

    /**
     * Determine whether a specific logging level is enabled.
     *
     * @param logLevel the level
     */
    def isLevelEnabled(logLevel: LogLevel) = level.value <= logLevel.value

    /**
     * Log a message at an arbitrary level.
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
                           t: Throwable): Unit =
        dispatch(true, logLevel, msg, t)

    private def dispatch(ok: => Boolean, 
                         logLevel: LogLevel,
                         msg: => AnyRef): Unit =
    {
        if (ok)
            dispatchToHandlers(LogMessage(name, System.currentTimeMillis,
                                          logLevel, msg, None))
    }

    private def dispatch(ok: => Boolean,
                         logLevel: LogLevel,
                         msg: => AnyRef,
                         t: Throwable): Unit =
    {
        if (ok)
            dispatchToHandlers(LogMessage(name, System.currentTimeMillis,
                                          logLevel, msg, Some(t)))
    }

    private def dispatchToHandlers(message: LogMessage) =
    {
        for (h <- handlers; if (h.level.value <= message.level.value))
            h.log(message)
    }
}

/**
 * A null logger.
 */
class NullLogger private[avsl](val name: String, val level: LogLevel)
extends Logger
{
    /**
     * Determine whether trace logging is enabled.
     */
    def isTraceEnabled = false

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def trace(msg: => AnyRef) = {}

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def trace(msg: => AnyRef, t: => Throwable) = {}

    /**
     * Determine whether debug logging is enabled.
     */
    def isDebugEnabled = false

    /**
     * Issue a debug logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def debug(msg: => AnyRef) = {}

    /**
     * Issue a debug logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def debug(msg: => AnyRef, t: => Throwable) = {}

    /**
     * Determine whether trace logging is enabled.
     */
    def isErrorEnabled = false

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def error(msg: => AnyRef) = {}

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def error(msg: => AnyRef, t: => Throwable) = {}

    /**
     * Determine whether trace logging is enabled.
     */
    def isInfoEnabled = false

    /**
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def info(msg: => AnyRef) = {}

    /**
     * Issue a trace logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def info(msg: => AnyRef, t: => Throwable) = {}

    /**
     * Determine whether trace logging is enabled.
     */
    def isWarnEnabled = false

    /**
     * Issue a warning logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def warn(msg: => AnyRef) = {}

    /**
     * Issue a warning logging message, with an exception.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     * @param t    the exception to include with the logged message.
     */
    def warn(msg: => AnyRef, t: => Throwable) = {}

    /**
     * Determine whether a specific logging level is enabled.
     *
     * @param logLevel the level
     */
    def isLevelEnabled(logLevel: LogLevel) = false

    /**
     * Log a message at an arbitrary level.
     *
     * @param logLevel  the level
     * @param msg       the message to log
     */
    def log(logLevel: LogLevel, msg: => AnyRef) = {}
}

/**
 * `Logger` companion object.
 */
object Logger
{
    private val handlers = MutableMap.empty[String, Handler]
    private val loggers = MutableMap.empty[String, Logger]
    private val formatters = MutableMap.empty[String, Formatter]

    val RootLoggerName = "root"

    val config = configure()
    val rootLogger = apply(RootLoggerName)

    /**
     * Get the named logger.
     *
     * @param name
     *
     * @return the logger
     */
    def apply(name: String): Logger =
    {
        def newLogger(logConfig: LoggerConfig): Logger =
        {
            val handlers = getHandlers(logConfig.handlerNames)
            val logger = new StandardLogger(name, logConfig.level, handlers)
            loggers += (name -> logger)
            logger
        }

        def findLogger(name: String): Logger =
        {
            config match
            {
                case None =>
                    new NullLogger(name, NoLogging)

                case Some(config) =>
                    newLogger(config.loggerConfigFor(name))
            }
        }

        loggers.synchronized
        {
            loggers.get(name) match
            {
                case None         => findLogger(name)
                case Some(logger) => logger
            }
        }
    }

    /**
     * Determine the appropriate level for the specified logger name.
     *
     * @param name  the logger name
     *
     * @return the level
     */
    def levelFor(name: String) = Trace

    private def configure(): Option[AVSLConfiguration] = AVSLConfiguration()

    private def getFormatter(name: String): Formatter =
    {
        def newFormatter(config: FormatterConfig) =
        {
            val cls = config.formatterClass
            val ctor = cls.getConstructor(classOf[ConfiguredArguments])
            val formatter = ctor.newInstance(config.args).
                                 asInstanceOf[Formatter]
            formatters += (name -> formatter)
            formatter
        }

        def findFormatter =
        {
            config match
            {
                case None      => new NullFormatter
                case Some(cfg) => newFormatter(cfg.formatters(name))
            }
        }

        formatters.get(name) match
        {
            case None            => findFormatter
            case Some(formatter) => formatter
        }
    }

    private def newHandler(config: HandlerConfig): Handler =
    {
        val cls = config.handlerClass
        val ctor = cls.getConstructor(classOf[ConfiguredArguments],
                                      classOf[Formatter],
                                      classOf[LogLevel])
        val formatter = getFormatter(config.formatterName)
        ctor.newInstance(config.args, formatter, config.level).
             asInstanceOf[Handler]
    }

    private def getHandlers(names: List[String]): List[Handler] =
     {
         def nameToHandler(name: String) = handlers.get(name) match
         {
             case Some(handler) =>
                 handler

             case None if (config == None) =>
                 new NullHandler(NoLogging)

             case None =>
                 newHandler(config.get.handlers(name))
         }

         names.map(nameToHandler(_))
     }
}
