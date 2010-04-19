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

package org.clapper.avsl.handler

import org.clapper.avsl.LogLevel

import java.util.Date

/**
 * Basic interface for a handler that dispatches log messages.
 */
trait Handler
{
    /**
     * The log level associated with the handler.
     */
    val level: LogLevel

    /**
     * Log a message, wherever the handler logs its output. The method will
     * only be called if the message's level is below or equal to the level
     * associated with the handler.
     *
     * @param name     the name (typically, the class name) of the component
     *                 issuing the message
     * @param datetime date/time associated with the message
     * @param level    the log level
     * @param msg      the object representing the message to log
     */
    def log(name: String, datetime: Date, level: LogLevel, msg: AnyRef): Unit

    /**
     * Log a message and an exception, wherever the handler logs its
     * output. The method will only be called if the message's level is
     * below or equal to the level associated with the handler.
     *
     * @param name     the name (typically, the class name) of the component
     *                 issuing the message
     * @param datetime date/time associated with the message
     * @param level    the log level
     * @param msg      the object representing the message to log
     * @param t        the exception
     */
    def log(name: String,
            datetime: Date,
            level: LogLevel,
            msg: AnyRef,
            t: Throwable): Unit

    /**
     * Log an exception, without a message, wherever the handler logs its
     * output. The method will only be called if the message's level is
     * below or equal to the level associated with the handler.
     *
     * The default implementation of this method just calls `log` with an
     * empty `msg` parameter.
     *
     * @param name     the name (typically, the class name) of the component
     *                 issuing the message
     * @param datetime date/time associated with the message
     * @param level    the log level
     * @param t        the exception
     */
    def log(name: String, datetime: Date, level: LogLevel, t: Throwable): Unit =
        log(name, datetime, level, "", t)
}
