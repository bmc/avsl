/*---------------------------------------------------------------------------*\
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
\*---------------------------------------------------------------------------*/

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.clapper.avsl._
import org.clapper.avsl.config.{ConfiguredArguments, NoConfiguredArguments}
import org.clapper.avsl.formatter._
import java.util.{Calendar, Date, Locale, TimeZone}

/**
 * Tests the formatter(s).
 */
class SimpleFormatterTest extends FlatSpec with ShouldMatchers
{
    val En_US = new Locale("en", "US")
    val ClassName = "org.clapper.avsl.SimpleFormatterTest"
    val ShortClassName = ClassName.split("""\.""").last
    val Level = Info
    val MessageText = "test message"
    val Millis = 284
    val calendarDate = Calendar.getInstance(En_US)
    calendarDate.set(2010, 2, 3, 20, 23, 00) // Wed, Mar 3, 2010

    // zero out the milliseconds and add our own.

    val time = (calendarDate.getTimeInMillis / 1000 * 1000) + Millis
    val date = new Date(time)

    "SimpleFormatter" should "map patterns correctly" in
    {
        val logMessage = LogMessage(ClassName, date, Level, MessageText, None)
        val data = List(
            ("1. %a", "1. Wed"),
            ("2. %A", "2. Wednesday"),
            ("3. %D", "3. 03/03/10"),
            ("4. %d", "4. 03"),
            ("5. %F", "5. 2010-03-03"),
            ("6. %H", "6. 20"),
            ("7. %h", "7. 08"),
            ("8. %j", "8. 62"), // 62nd Julian day of the year
            ("9. %l", "9. " + Level.label),
            ("10. %L", "10. " + Level.value.toString),
            ("11. %M", "11. 23"),
            ("12. %m", "12. 03"),
            ("13. %n", "13. " + ShortClassName),
            ("14. %N", "14. " + ClassName),
            ("15. %s", "15. 00"),
            ("16. %S", "16. " + Millis.toString),
            ("17. %t", "17. " + MessageText),
            ("18. %y", "18. 10"),
            ("19. %Y", "19. 2010"),
            ("20. %%", "20. %"),
            ("21. [%H:%M:%s] %l %n %t", "21. [20:23:00] " + Level + " " +
                                        ShortClassName + " " + MessageText)
        )

        for ((pattern, result) <- data)
        {
            val args = new ConfiguredArguments(Map("format" -> pattern))
            val formatter = new SimpleFormatter(args)
            formatter.formatString should equal (pattern)
            formatter.format(logMessage) should equal (result)
        }
    }

    it should "use default pattern is none is given" in
    {
        val formatter = new SimpleFormatter(NoConfiguredArguments)
        formatter.formatString should equal (formatter.DefaultFormat)
    }
}
