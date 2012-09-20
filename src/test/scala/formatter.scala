/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2010 Brian M. Clapper. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.

  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

  * Neither the names "clapper.org", "Scalasti", nor the names of its
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

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.clapper.avsl._
import org.clapper.avsl.config.{ConfiguredArguments, NoConfiguredArguments}
import org.clapper.avsl.formatter._

import grizzled.math.stats._
import Numeric._

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, Locale, TimeZone}

/**
 * Tests the formatter(s).
 */
class SimpleFormatterTest extends FlatSpec with ShouldMatchers {
  val En_US = new Locale("en", "US")
  val ClassName = "org.clapper.avsl.SimpleFormatterTest"
  val ShortClassName = ClassName.split("""\.""").last
  val Level = LogLevel.Info
  val MessageText = "test message"
  val Millis = 284
  val calendarDate = Calendar.getInstance(En_US)
  calendarDate.set(2010, 2, 3, 20, 23, 0) // Wed, Mar 3, 2010
  // Use the current time zone
  calendarDate.setTimeZone(TimeZone.getDefault)

  // zero out the milliseconds and add our own.

  val time = (calendarDate.getTimeInMillis / 1000 * 1000) + Millis

  "SimpleFormatter" should "map patterns correctly" in {
    val strTZ = new SimpleDateFormat("z").format(calendarDate.getTime)
    val currentThreadName = Thread.currentThread.getName
    val logMessage = LogMessage(ClassName, time, Level, MessageText, None)
    val data = List(
      ("(a) %a", "(a) Wed"),
      ("(A) %A", "(A) Wednesday"),
      ("(b) %b", "(b) Mar"),
      ("(B) %B", "(B) March"),
      ("(D) %D", "(D) 03/03/10"),
      ("(d) %d", "(d) 03"),
      ("(F) %F", "(F) 2010-03-03"),
      ("(H) %H", "(H) 20"),
      ("(h) %h", "(h) 08"),
      ("(j) %j", "(j) 62"), // 62nd Julian day of the year
      ("(l) %l", "(l) " + Level.label),
      ("(L) %L", "(L) " + Level.value.toString),
      ("(M) %M", "(M) 23"),
      ("(m) %m", "(m) 03"),
      ("(n) %n", "(n) " + ShortClassName),
      ("(N) %N", "(N) " + ClassName),
      ("(s) %s", "(s) 00"),
      ("(S) %S", "(S) " + Millis.toString),
      ("(t) %t", "(t) " + MessageText),
      ("(T) %T", "(T) " + currentThreadName),
      ("(z) %z", "(z) " + strTZ),
      ("(y) %y", "(y) 10"),
      ("(Y) %Y", "(Y) 2010"),
      ("(pct) %%", "(pct) %"),
      ("[%H:%M:%s] %l %n %t", "[20:23:00] " + Level + " " +
       ShortClassName + " " + MessageText)
    )

    for ((pattern, result) <- data) {
      val args = new ConfiguredArguments(Map("format" -> pattern))
      val formatter = new SimpleFormatter(args)
      formatter.formatString should equal (pattern)
      formatter.format(logMessage) should equal (result)
    }
  }

  it should "use default pattern is none is given" in {
    val formatter = new SimpleFormatter(NoConfiguredArguments)
    formatter.formatString should equal (formatter.DefaultFormat)
  }

  it should "perform acceptably" in {
    val Data = List("%a",
                    "%A",
                    "%b",
                    "%B",
                    "%D",
                    "%d",
                    "%F",
                    "%H",
                    "%h",
                    "%j",
                    "%l",
                    "%L",
                    "%M",
                    "%m",
                    "%n",
                    "%N",
                    "%s",
                    "%S",
                    "%t",
                    "%T",
                    "%z",
                    "%y",
                    "%Y")

    import scala.collection.mutable.{ListBuffer, Map => MutableMap}

    var longestTime: Long = 0
    var shortestTime: Long = Long.MaxValue
    val Total = 100000
    var times = ListBuffer.empty[Long]
    val logMessage = LogMessage(ClassName, time, Level, MessageText, None)

    for (i <- 1 to Total) {
      import scala.collection.JavaConversions._

      val dataJList = java.util.Arrays.asList(Data: _*)
      java.util.Collections.shuffle(dataJList)
      val pattern = dataJList mkString " "
      val args = new ConfiguredArguments(Map("format" -> pattern))

      val start = System.currentTimeMillis
      val formatter = new SimpleFormatter(args)
      formatter format logMessage
      val end = System.currentTimeMillis

      val elapsed = end - start
      longestTime = scala.math.max(longestTime, elapsed)
      shortestTime = scala.math.min(shortestTime, elapsed)
      times += elapsed
    }

    val totalElapsed = times.foldLeft(0l)(_ + _)
    val timesList = times.toList

    println("total time        = " + totalElapsed + " ms")
    println("total iterations  = " + Total)
    println("shortest duration = " + shortestTime + " ms")
    println("longest duration  = " + longestTime + " ms")
    println("mean              = " + arithmeticMean(timesList: _*))
    println("median            = " + median(timesList: _*))
    println("mode(s)           = " + mode(timesList: _*).mkString(", "))
    println("stddev            = " + popStdDev(timesList: _*))
    println("harmonic mean     = " + harmonicMean(timesList: _*))
  }
}
