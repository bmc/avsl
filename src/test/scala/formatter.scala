import org.scalatest.{FlatSpec, Matchers}
import org.clapper.avsl._
import org.clapper.avsl.config.{ConfiguredArguments, NoConfiguredArguments}
import org.clapper.avsl.formatter._
import grizzled.math.stats._

import Numeric._
import java.text.SimpleDateFormat
import java.util.{Calendar, Locale, TimeZone}

import scala.util.Random

/**
 * Tests the formatter(s).
 */
class SimpleFormatterTest extends FlatSpec with Matchers {
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
    val logMessage = LogMessage(ClassName, time, Level, MessageText, None)
    require(Total > 1)

    val times = for (i <- 1 to Total) yield {
      val shuffledData = Random.shuffle(Data)
      val pattern = shuffledData mkString " "
      val args = new ConfiguredArguments(Map("format" -> pattern))

      val start = System.currentTimeMillis
      val formatter = new SimpleFormatter(args)
      formatter format logMessage
      val end = System.currentTimeMillis

      val elapsed = end - start
      longestTime = scala.math.max(longestTime, elapsed)
      shortestTime = scala.math.min(shortestTime, elapsed)

      elapsed
    }

    val totalElapsed = times.foldLeft(0L)(_ + _)
    val (firstTime, remainingTimes) = (times.head, times.tail)

    println("total time        = " + totalElapsed + " ms")
    println("total iterations  = " + Total)
    println("shortest duration = " + shortestTime + " ms")
    println("longest duration  = " + longestTime + " ms")
    println("mean              = " + arithmeticMean(firstTime, remainingTimes: _*))
    println("median            = " + median(firstTime, remainingTimes: _*))
    println("mode(s)           = " + mode(firstTime, remainingTimes: _*))
    println("stddev            = " + popStdDev(firstTime, remainingTimes: _*))
    println("harmonic mean     = " + harmonicMean(firstTime, remainingTimes: _*))
  }
}
