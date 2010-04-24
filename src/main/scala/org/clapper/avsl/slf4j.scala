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
 * AVSL SLF4J logging compatibility.
 */
package org.clapper.avsl.slf4j

import org.clapper.avsl._

import org.slf4j.{Logger => SLF4JLogger}
import org.slf4j.Marker;
import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.{MessageFormatter => Formatter}
import org.slf4j.helpers.MarkerIgnoringBase;

/**
 * A trait that, mixed in, provides SLF4J methods for an AVSL Logger class.
 * Kept separate, for clarity. `Marker` methods ignore the marker parameters.
 */
class AVSL_SLF4J_Logger(logger: Logger) extends MarkerIgnoringBase
{
    override def getName(): String = logger.name

    def isDebugEnabled(): Boolean = logger.isDebugEnabled

    def debug(fmt: String, arg: Object): Unit =
        if (isDebugEnabled) logger.debug(Formatter.format(fmt, arg))

    def debug(fmt: String, args: Array[Object]): Unit =
        if (isDebugEnabled) logger.debug(Formatter.format(fmt, args))

    def debug(fmt: String, arg1: Object, arg2: Object): Unit =
        if (isDebugEnabled) logger.debug(Formatter.format(fmt, arg1, arg2))

    def debug(msg: String): Unit = logger.debug(msg)

    def debug(msg: String, t: Throwable): Unit = logger.debug(msg, t)

    def isErrorEnabled(): Boolean = logger.isErrorEnabled

    def error(fmt: String, arg: Object): Unit =
        if (isErrorEnabled) logger.error(Formatter.format(fmt, arg))

    def error(fmt: String, args: Array[Object]): Unit =
        if (isErrorEnabled) logger.error(Formatter.format(fmt, args))

    def error(fmt: String, arg1: Object, arg2: Object): Unit =
        if (isErrorEnabled) logger.error(Formatter.format(fmt, arg1, arg2))

    def error(msg: String): Unit = logger.error(msg)

    def error(msg: String, t: Throwable): Unit = logger.error(msg, t)

    def isInfoEnabled(): Boolean = logger.isInfoEnabled

    def info(fmt: String, arg: Object): Unit =
        if (isInfoEnabled) logger.info(Formatter.format(fmt, arg))

    def info(fmt: String, args: Array[Object]): Unit =
        if (isInfoEnabled) logger.info(Formatter.format(fmt, args))

    def info(fmt: String, arg1: Object, arg2: Object): Unit =
        if (isInfoEnabled) logger.info(Formatter.format(fmt, arg1, arg2))

    def info(msg: String): Unit = logger.info(msg)

    def info(msg: String, t: Throwable): Unit = logger.info(msg, t)

    def isTraceEnabled(): Boolean = logger.isTraceEnabled

    def trace(fmt: String, arg: Object): Unit =
        if (isTraceEnabled) logger.trace(Formatter.format(fmt, arg))

    def trace(fmt: String, args: Array[Object]): Unit =
        if (isTraceEnabled) logger.trace(Formatter.format(fmt, args))

    def trace(fmt: String, arg1: Object, arg2: Object): Unit =
        if (isTraceEnabled) logger.trace(Formatter.format(fmt, arg1, arg2))

    def trace(msg: String): Unit = logger.trace(msg)

    def trace(msg: String, t: Throwable): Unit = logger.trace(msg, t)

    def isWarnEnabled(): Boolean = logger.isWarnEnabled

    def warn(fmt: String, arg: Object): Unit =
        if (isWarnEnabled) logger.warn(Formatter.format(fmt, arg))

    def warn(fmt: String, args: Array[Object]): Unit =
        if (isWarnEnabled) logger.warn(Formatter.format(fmt, args))

    def warn(fmt: String, arg1: Object, arg2: Object): Unit =
        if (isWarnEnabled) logger.warn(Formatter.format(fmt, arg1, arg2))

    def warn(msg: String): Unit = logger.warn(msg)

    def warn(msg: String, t: Throwable): Unit = logger.warn(msg, t)
}

/**
 * SLF4J logger factory.
 */
class AVSL_SLF4J_LoggerFactory extends ILoggerFactory
{
    def getLogger(name: String): AVSL_SLF4J_Logger =
        new AVSL_SLF4J_Logger(Logger(name))
}
