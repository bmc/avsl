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

import java.util.{Calendar, Date, Locale, TimeZone}
import java.text.{DateFormat, SimpleDateFormat}

/**
  * `SimpleFormatter` represents the default formatter for the AVSL
  * logger. It uses simple %-escaped format strings, akin to the standard
  * C `strftime()` function. These escapes, described below, are more compact
  * than the format strings used by Java's `SimpleDateFormat` class; they
  * also don't suffer from the odd quoting conventions imposed by
  * `SimpleDateFormat`. However, they are mapped to `SimpleDateFormat`
  * patterns, so they are locale-, language-, and time zone-sensitive.
  *
  * A `SimpleFormatter` accepts the following name/value pair arguments:
  *
  * - `format`: The format to use. If not specified, there's a reasonable
  *    default
  * - `language`: The language to use when formatting dates, using the Java
  *   `Locale` values. If not specified, the default locale is used.
  * - `country`: The country to use when formatting dates, using the Java
  *  `Locale` values. If not specified, the default locale is used.
  * - `tz`: The time zone to use. If not specified, the default is used.
  *
  * The recognized format escapes are shown below. Anything else is displayed
  * literally. Many of the escapes are borrowed directly from `strftime()`.
  *
  * - %a: the short day-of-week name (e.g., "Wed")
  * - %A: the long day-of-week name (e.g., "Wednesday")
  * - %b: the abbreviated month name (e.g., "Mar", "Nov")
  * - %B: the full month name (e.g., "March", "November")
  * - %d: the day of the month
  * - %D: equivalent to %m/%d/%y
  * - %F: equivalent to %Y/%m/%d
  * - %h: the hour of the day (0-12)
  * - %H: the hour of the day (1-23)
  * - %j: the day of the year (i.e., the so-called Julian day)
  * - %l: the log level name (e.g., "INFO", "DEBUG")
  * - %L: the log level's numeric value
  * - %m: the month number (01-12)
  * - %M: the current minute, zero-padded
  * - %n: the short name of the logger (i.e., the last part of the class name)
  * - %N: the full name of the logger (i.e., the class name)
  * - %s: the current second, zero-padded
  * - %S: the current millisecond, zero-padded
  * - %t: the text of the log message
  * - %T: the current thread name
  * - %y: the 2-digit year
  * - %Y: the full 4-digit year
  * - %z: the time zone name (e.g., "UTC", "PDT", "EST")
  * - %%: a literal "%"
  */
class SimpleFormatter(args: ConfiguredArguments) extends Formatter {
  import java.text.SimpleDateFormat

  val DefaultFormat = "[%Y/%m/%d %H:%M:%s:%S] %l %n %t"
  val formatString = args.getOrElse("format", DefaultFormat)
  val defaultLocale = Locale.getDefault
  val language = args.getOrElse("language", defaultLocale.getLanguage)
  val country = args.getOrElse("country", defaultLocale.getCountry)

  val tz = args.get("tz") match {
    case None         => TimeZone.getDefault
    case Some(tzName) => TimeZone.getTimeZone(tzName)
  }


  // Must be lazy, to ensure that they is evaluated after the variables,
  // above, are initialized.
  lazy val locale = new Locale(language, country)
  private lazy val dateFormat = new ParsedPattern(formatString, locale, tz)

  def format(logMessage: LogMessage): String = {
    logMessage.exception match {
      case None =>
        dateFormat.format(logMessage)

      case Some(t) => {
        import java.io.{PrintWriter, StringWriter}

        val sw = new StringWriter
        t.printStackTrace(new PrintWriter(sw))
        dateFormat.format(logMessage) + " " + sw.toString
      }
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
private class ParsedPattern(originalPattern: String,
                            locale: Locale,
                            tz: TimeZone) {
  val parsedPattern: List[(LogMessage) => String] =
    parse(originalPattern.toList)

  private lazy val Mappings = Map[Char, LogMessage => String](
    'a' -> datePatternFunc("E"),
    'A' -> datePatternFunc("EEEE"),
    'b' -> datePatternFunc("MMM"),
    'B' -> datePatternFunc("MMMM"),
    'd' -> datePatternFunc("dd"),
    'D' -> datePatternFunc("MM/dd/yy"),
    'F' -> datePatternFunc("yyyy-MM-dd"),
    'h' -> datePatternFunc("hh"),
    'H' -> datePatternFunc("HH"),
    'j' -> datePatternFunc("D"),
    'l' -> insertLevelName _,
    'L' -> insertLevelValue _,
    'M' -> datePatternFunc("mm"),
    'm' -> datePatternFunc("MM"),
    'n' -> insertName(true) _,
    'N' -> insertName(false) _,
    's' -> datePatternFunc("ss"),
    'S' -> datePatternFunc("SSS"),
    't' -> insertMessage _,
    'T' -> insertThreadName _,
    'y' -> datePatternFunc("yy"),
    'Y' -> datePatternFunc("yyyy"),
    'z' -> datePatternFunc("z"),
    '%' -> copyLiteralFunc("%")
  )

  /** Format a log message, using the parsed pattern.
    *
    * @param logMessage the message
    *
    * @return the formatted string
    */
  def format(logMessage: LogMessage): String =
    parsedPattern.map(_(logMessage)).mkString("")

  override def toString = originalPattern

  private def insertThreadName(msg: LogMessage): String =
    Thread.currentThread.getName

  private def insertLevelValue(msg: LogMessage): String =
    msg.level.value.toString

  private def insertLevelName(msg: LogMessage): String = msg.level.label

  private def insertMessage(msg: LogMessage): String = msg.message.toString

  private def insertName(short: Boolean)(msg: LogMessage): String =
    if (short) msg.name.split("""\.""").last else msg.name

  private def insertDateChunk(format: DateFormat)(msg: LogMessage): String = {
    val cal = Calendar.getInstance(tz, locale)
    cal.setTimeInMillis(msg.date)
    format.format(cal.getTime)
  }

  private def datePatternFunc(pattern: String) =
    insertDateChunk(new SimpleDateFormat(pattern, locale)) _

  private def copyLiteral(s: String)(msg: LogMessage): String = s

  private def copyLiteralFunc(s: String) = copyLiteral(s) _

  private def escape(ch: Char): List[LogMessage => String] =
    List(Mappings.getOrElse(ch, copyLiteralFunc("'%" + ch + "'")))

  private def parse(stream: List[Char], gathered: String = ""):
  List[LogMessage => String] = {

    def gatheredFuncList = {
      if (gathered == "") Nil else List(copyLiteralFunc(gathered))
    }

    stream match {
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
