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
import org.clapper.avsl.config._
import org.clapper.avsl.handler.NullHandler
import scala.io.Source

/**
 * Tests the configuration class.
 */
class ConfigTest extends FlatSpec with ShouldMatchers {

  private def loadConfig(s: String) =
    new AVSLConfiguration(Source.fromString(s))

  "Configuration" should "load a valid configuration" in {
    val config = """
    |[logger_root]
    |level: info
    |handlers: h1
    |[handler_h1]
    |level: info
    |class: NullHandler
    |formatter: f1
    |[formatter_f1]
    |class: NullFormatter
    """.stripMargin

    loadConfig(config)
  }

  it should "abort if a logger section name is bad" in {
    val config = """
    |[logger_]
    |level: info
    """.stripMargin

    evaluating { loadConfig(config) } should
    produce [AVSLConfigSectionException]
  }

  it should "abort if a handler section name is bad" in {
    val config = """
    |[logger_root]
    |level: info
    |[handler_]
    """.stripMargin

    evaluating { loadConfig(config) } should
    produce [AVSLConfigSectionException]
  }

  it should "abort if a formatter section name is bad" in {
    val config = """
    |[logger_root]
    |level: info
    |[handler_h1]
    |level: trace
    |class: ConsoleHandler
    |formatter: f1
    |[formatter_]
    |class: DefaultFormatter
    |format: [%Y/%M/%d %h:%m:%s:%S] (%l) %t
    """.stripMargin

    evaluating { loadConfig(config) } should
    produce [AVSLConfigSectionException]
  }

  it should "abort if a handler is missing a formatter" in {
    val config = """
    |[handler_h1]
    |level: trace
    |class: NullHandler
    """.stripMargin

    evaluating { loadConfig(config) } should
    produce [AVSLConfigSectionException]
  }

  it should "abort if a handler specifies a bad formatter" in {
    val config = """
    |[handler_h1]
    |level: trace
    |class: NullHandler
    |formatter: foo
    """.stripMargin

    evaluating { loadConfig(config) } should
    produce [AVSLConfigException]
  }

  it should "abort if a logger specifies to a bad handler" in {
    val config = """
    |[logger_foo]
    |pattern: org.clapper.avsl
    |level: info
    |handlers: h1, h2
    """.stripMargin

    evaluating { loadConfig(config) } should
    produce [AVSLConfigException]
  }

  it should "abort if a logger has no pattern" in {
    val config = """
    |[logger_foo]
    |level: info
    |handlers: h1, h2
    """.stripMargin

    evaluating { loadConfig(config) } should
    produce [AVSLConfigSectionException]
  }

  it should "parse a logger section properly" in {
    val config = """
    |[logger_foo]
    |level: info
    |handlers: h1, h2
    |pattern: org.clapper.foo
    |[handler_h1]
    |level: trace
    |class: ConsoleHandler
    |formatter: f1
    |[handler_h2]
    |level: trace
    |class: ConsoleHandler
    |formatter: f1
    |[formatter_f1]
    |class: DefaultFormatter
    |format: [%Y/%M/%d %h:%m:%s:%S] (%l) %t
    """.stripMargin

    val cfg = loadConfig(config)
    val loggerConfig = cfg.loggerConfigFor("org.clapper.foo")
    assert(loggerConfig != null)
    loggerConfig.name should equal ("foo")
    loggerConfig.pattern should equal ("org.clapper.foo")
    loggerConfig.handlerNames should equal (List("h1", "h2"))
    loggerConfig.level should equal (LogLevel.Info)
  }

  it should "parse a handler section properly" in {
    val config = """
    |[handler_h1]
    |level: trace
    |class: NullHandler
    |formatter: f1
    |[formatter_f1]
    |class: DefaultFormatter
    |format: [%Y/%M/%d %h:%m:%s:%S] (%l) %t
    """.stripMargin

    val cfg = loadConfig(config)
    val handlerConfig = cfg.handlers("h1")
    assert(handlerConfig != null)
    handlerConfig.handlerClass should equal (classOf[NullHandler])
    handlerConfig.formatterName should equal ("f1")
    handlerConfig.level should equal (LogLevel.Trace)
  }
}
