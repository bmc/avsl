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

import org.clapper.avsl.{LogLevel, LogMessage}
import org.clapper.avsl.config.ConfiguredArguments

import java.util.Date
import java.text.{DateFormat, SimpleDateFormat}

class SimpleFormatter(args: ConfiguredArguments) extends Formatter
{
    import java.text.SimpleDateFormat

    val DefaultFormat = "[%Y/%M/%d %H:%m:%s:%S] %l %c %t"
    val formatString = args.getOrElse("format", DefaultFormat)

    // Must be lazy, to ensure that it is evaluated after formatString
    // is initialized.
    private lazy val dateFormat = new ParsedPattern(formatString)

    def format(logMessage: LogMessage): String =
    {
        logMessage.exception match
        {
            case None =>
                dateFormat.format(logMessage)

            case Some(t) =>
                import java.io.{PrintWriter, StringWriter}
                val sw = new StringWriter

                t.printStackTrace(new PrintWriter(sw))
                format(logMessage) + " " + sw.toString
        }
    }
}

/**
 * A parsed format. In the parsed format, the format is broken into tokens,
 * each of which is associated with a function. Formatting a message means
 * means passing the message to all the functions and then concatenating
 * the result. This approach avoids odd escaping problems with Java's
 * `SimpleDateFormat` strings, because each token is separately handled,
 * and literals are extracted and processed independently of format
 * strings. Storing the functions, rather than the strings, means we can
 * curry the functions and create the `SimpleDateFormat` objects ahead of
 * time.
 */
private class ParsedPattern(originalPattern: String)
{
    val parsedPattern: List[(LogMessage) => String] =
        parse(originalPattern.toList)

    private lazy val Mappings = Map[Char, LogMessage => String](
        'a' -> datePatternFunc("E"),
        'A' -> datePatternFunc("EEEE"),
        'b' -> datePatternFunc("MM"),
        'B' -> datePatternFunc("MMMM"),
        'D' -> datePatternFunc("MM/dd/yy"),
        'd' -> datePatternFunc("dd"),
        'F' -> datePatternFunc("yyyy-MM-dd"),
        'H' -> datePatternFunc("HH"),
        'h' -> datePatternFunc("hh"),
        'j' -> datePatternFunc("D"),
        'L' -> insertLevelValue _,
        'l' -> insertLevelName _,
        'M' -> datePatternFunc("mm"),
        'm' -> datePatternFunc("MM"),
        'n' -> insertName(true) _,
        'N' -> insertName(false) _,
        's' -> datePatternFunc("ss"),
        'S' -> datePatternFunc("SSS"),
        't' -> insertMessage _,
        'y' -> datePatternFunc("yy"),
        'Y' -> datePatternFunc("yyyy"),
        '%' -> copyLiteralFunc("%")
    )

    /**
     * Format a log message, using the parsed pattern.
     *
     * @param logMessage the message
     *
     * @return the formatted string
     */
    def format(logMessage: LogMessage): String =
        parsedPattern.map(_(logMessage)).mkString("")

    override def toString = originalPattern

    private def insertLevelValue(logMessage: LogMessage): String =
        logMessage.level.value.toString

    private def insertLevelName(logMessage: LogMessage): String =
        logMessage.level.label

    private def insertMessage(logMessage: LogMessage): String =
        logMessage.message.toString

    private def insertName(short: Boolean)(logMessage: LogMessage): String =
        if (short) logMessage.name.split("""\.""").last else logMessage.name

    private def insertDateChunk(format: DateFormat)
                               (logMessage: LogMessage): String =
        format.format(logMessage.date)

    private def datePatternFunc(pattern: String) =
        insertDateChunk(new SimpleDateFormat(pattern)) _

    private def copyLiteral(s: String)(logMessage: LogMessage): String = s
    private def copyLiteralFunc(s: String) = copyLiteral(s) _

    private def escape(ch: Char): List[LogMessage => String] =
        List(Mappings.getOrElse(ch, copyLiteralFunc("'%" + ch + "'")))

    private def parse(stream: List[Char], gathered: String = ""):
        List[LogMessage => String] =
    {
        def gatheredFuncList = 
            if (gathered == "") Nil else List(copyLiteralFunc(gathered))

        stream match
        {
            case Nil if (gathered != "") =>
                List(copyLiteralFunc(gathered))

            case Nil =>
                Nil

            case '%' :: Nil =>
                gatheredFuncList ::: List(copyLiteralFunc("%"))

            case '%' :: tail =>
                gatheredFuncList ::: escape(tail(0)) ::: parse(tail drop 1)

            case c :: tail =>
                parse(tail, gathered + c)
        }
    }
}
