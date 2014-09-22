/*
  ---------------------------------------------------------------------------
  This software is released under a BSD license, adapted from
  http://opensource.org/licenses/bsd-license.php

  Copyright (c) 2010-2014 Brian M. Clapper. All rights reserved.

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
