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

import org.clapper.avsl.handler.Handler
import java.util.Date
import scala.collection.mutable.{Map => MutableMap, Set => MutableSet}

abstract sealed class LogLevel(val id: String, val value: Int)
{
    override def toString = id
}

case object NoLogging extends LogLevel("NoLogging", 0)
case object Trace extends LogLevel("TRACE", 10)
case object Debug extends LogLevel("DEBUG", 20)
case object Info extends LogLevel("INFO", 30)
case object Warn extends LogLevel("WARN", 40)
case object Error extends LogLevel("ERROR", 50)
case object All extends LogLevel("All", Error.value)

/**
 * The basic logger class. This class provides its own Scala-friendly
 * methods, as well as non-marker SLF4J methods.
 */
class Logger private[avsl] (val name: String, val level: LogLevel)
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
     * Issue a trace logging message.
     *
     * @param msg  the message object. `toString()` is called to convert it
     *             to a loggable string.
     */
    def warn(msg: => AnyRef): Unit =
        dispatch(isWarnEnabled, Warn, msg)

    /**
     * Issue a trace logging message, with an exception.
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
        if (ok) Logger.dispatch(name, logLevel, msg)

    private def dispatch(ok: => Boolean,
                         logLevel: LogLevel,
                         msg: => AnyRef,
                         t: Throwable): Unit =
        if (ok) Logger.dispatch(name, logLevel, msg, t)
}

object Logger
{
    private val loggers = MutableMap.empty[String, Logger]
    private val handlers = MutableSet.empty[Handler]

    val RootLoggerName = "ROOT"

    configure()
    logger(RootLoggerName)

    /**
     * Get the named logger.
     *
     * @param name
     *
     * @return the logger
     */
    def logger(name: String): Logger =
    {
        def newLogger =
        {
            val logger = new Logger(name, levelFor(name))
            loggers += (name -> logger)
            logger
        }

        loggers.synchronized
        {
            loggers.get(name) match
            {
                case Some(logger) => logger
                case None         => newLogger
            }
        }
    }

    /**
     * Add a message handler to the list of handlers.
     *
     * @param handler  the handler
     */
    def addHandler(handler: Handler): Unit = handlers.add(handler)

    /**
     * Determine the appropriate level for the specified logger name.
     *
     * @param name  the logger name
     *
     * @return the level
     */
    def levelFor(name: String) = Trace

    private[avsl] def dispatch(name: String,
                               level: LogLevel,
                               msg: AnyRef): Unit =
    {
        val now = new Date
        for (h <- handlers; if (h.level.value <= level.value))
            h.log(name, now, level, msg)
    }

    private[avsl] def dispatch(name: String,
                               level: LogLevel,
                               msg: AnyRef,
                               t: Throwable): Unit =
    {
        val now = new Date
        for (h <- handlers; if (h.level.value <= level.value))
            h.log(name, now, level, msg, t)
    }

    private def configure(): Unit =
    {
        // temporary
        import handler.ConsoleHandler
        import formatter.DefaultFormatter

        addHandler(new ConsoleHandler(DefaultFormatter, Trace))
    }
}
