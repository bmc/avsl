import org.scalatest.{Matchers, FlatSpec}
import org.clapper.avsl._
import org.clapper.avsl.config._
import org.clapper.avsl.handler.{EmailHandler, NullHandler}
import org.clapper.avsl.formatter.NullFormatter
import scala.io.Source

class EmailHandlerSpec extends FlatSpec with Matchers  {

  val GoodConfiguration = new ConfiguredArguments(
    Map(
      "smtp.port"   -> "25",
      "recipients"  -> "foo@example.com",
      "smtp.server" -> "smtp.example.com",
      "sender"      -> "foo@example.org"
    )
  )

  private def modifiedConfig(config: ConfiguredArguments,
                             args: (String, String)*) = {
    val args2 = args.filter { case (k, v) => v.length > 0 }
    new ConfiguredArguments(config.toMap ++ args2)
  }

  private def deleteKeys(config: ConfiguredArguments, keys: String*):
    ConfiguredArguments = {

    val keySet = Set(keys: _*)
    val newArgs = config.toMap.filter { case (k, v) => ! (keySet.contains(k))}
    new ConfiguredArguments(newArgs)
  }

  "EmailHandler" should "be instantiated properly with a good config" in {
    new EmailHandler(GoodConfiguration, new NullFormatter, LogLevel.Debug)
  }

  it should "abort if the SMTP port is not numeric" in {
    an [AVSLConfigException] should be thrownBy {
      val args = modifiedConfig(GoodConfiguration, "smtp.port" -> "foo")
      new EmailHandler(args, new NullFormatter, LogLevel.Debug)
    }
  }

  it should "abort if the sender is not specified" in {
    an [AVSLConfigException] should be thrownBy {
      val args = deleteKeys(GoodConfiguration, "sender")
      new EmailHandler(args, new NullFormatter, LogLevel.Debug)
    }
  }

  it should "abort if there are no recipients" in {
    an [AVSLConfigException] should be thrownBy {
      val args = deleteKeys(GoodConfiguration, "recipients")
      new EmailHandler(args, new NullFormatter, LogLevel.Debug)
    }
  }

  it should "be instantiated properly with multiple recipients" in {
    import javax.mail.Address
    import javax.mail.internet.InternetAddress

    val args = modifiedConfig(GoodConfiguration,
                              "recipients" -> "foo@example.org, bar@foo.com")
    val handler = new EmailHandler(args, new NullFormatter, LogLevel.Debug)
    val expected = InternetAddress.parse("foo@example.org, bar@foo.com", true).
                                   map(_.asInstanceOf[Address])
    handler.recipients should equal(expected)
  }
}
