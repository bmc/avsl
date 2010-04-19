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

package org.clapper.avsl.formatter

import org.clapper.avsl.LogLevel
import java.util.Date

/**
 * Basic interface for a message formatter.
 */
trait Formatter
{
    def format(name: String,
               datetime: Date,
               level: LogLevel,
               msg: AnyRef): String
    def format(name: String,
               datetime: Date,
               level: LogLevel,
               msg: AnyRef,
               t: Throwable): String
}

class SimpleFormatter(args: Map[String, String]) extends Formatter
{
    import java.text.SimpleDateFormat

    private val DefaultFormat = "[%Y/%M/%d %H:%m:%s:%S] %L %t"

    private val expandedFormat = args.getOrElse("format", DefaultFormat).
                                      replaceAll("%y", "yy").
                                      replaceAll("%Y", "yyyy").
                                      replaceAll("%M", "MM").
                                      replaceAll("%d", "dd").
                                      replaceAll("%H", "HH").
                                      replaceAll("%h", "hh").
                                      replaceAll("%m", "mm").
                                      replaceAll("%s", "ss").
                                      replaceAll("%S", "SSS").
                                      replaceAll("%L", "'level.id'").
                                      replaceAll("%l", "'level.value'").
                                      replaceAll("%t", "'message'").
                                      replaceAll("%C", "'class.full'").
                                      replaceAll("%c", "'class.short'").
                                      replaceAll("%%", "'percent'")
    private val dateFormat = new SimpleDateFormat(expandedFormat)

    def format(name: String,
               datetime: Date,
               level: LogLevel,
               msg: AnyRef): String =
    {
        val s = dateFormat.format(datetime)
        s.replaceAll("level.id", level.id).
          replaceAll("level.value", String.valueOf(level.value)).
          replaceAll("message", String.valueOf(msg)).
          replaceAll("percent", "%").
          replaceAll("class.long", name).
          replaceAll("class.short", name.split("""\.""").last).
          replaceAll("percent", "%")
    }

    def format(name: String,
               datetime: Date,
               level: LogLevel,
               msg: AnyRef,
               t: Throwable) =
    {
        import java.io.{PrintWriter, StringWriter}
        val sw = new StringWriter

        t.printStackTrace(new PrintWriter(sw))
        format(name, datetime, level, msg) + " " + sw.toString
    }
}

/**
 * The default formatter, if none is specified.
 */
object DefaultFormatter extends SimpleFormatter(Map.empty[String, String])

