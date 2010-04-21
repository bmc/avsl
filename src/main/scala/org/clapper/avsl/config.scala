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

/**
 * AVSL logging classes.
 */
package org.clapper.avsl

import org.clapper.avsl.formatter._
import org.clapper.avsl.handler._

import grizzled.config.{Configuration, Section}
import scala.io.Source
import scala.collection.mutable.{Map => MutableMap, Set => MutableSet}
import java.net.{MalformedURLException, URL}
import java.io.File

/*---------------------------------------------------------------------------*\
                                  Classes
\*---------------------------------------------------------------------------*/

/**
 * The configuration handler.
 */
//private[avsl] 
class AVSLConfiguration(val url: URL) extends Configuration
{
    load(Source.fromURL(url))

    val loggers = getLoggers
    val handlers = getHandlers
    val formatters = getFormatters

    validate

    private def validate =
    {
        val errorMessage = validateLoggers.getOrElse("") +
                           validateFormatters.getOrElse("") +
                           validateHandlers.getOrElse("")
        if (errorMessage != "")
            throw new AVSLConfigException(errorMessage)
    }

    private def mapString(s: String): Option[String] =
        if (s == "") None else Some(s)

    private def getLoggers: Map[String, LoggerConfig] =
    {
        val configs = matchingSections("^logger_[a-zA-Z_0-9]+$".r).
                      map(new LoggerConfig(this, _))

        Map.empty[String, LoggerConfig] ++ configs.map(cfg => (cfg.name, cfg))
    }

    private def validateLoggers: Option[String] =
    {
        val errorMessages = 
            for {l <- loggers.values
                 h <- l.handlerNames}
            yield
            {
                if (handlers.get(h) == None)
                    "Logger \"" + l.name + "\" refers to unknown handler \"" +
                    h + "\""

                else
                    ""
            }

        mapString(errorMessages mkString "")
    }

    private def getFormatters: Map[String, FormatterConfig] =
    {
        val configs = matchingSections("^formatter_[a-zA-Z_0-9]+$".r).
                      map(new FormatterConfig(this, _))
        Map.empty[String, FormatterConfig] ++ configs.map(c => (c.name, c))
    }

    private def validateFormatters: Option[String] = None

    private def getHandlers: Map[String, HandlerConfig] =
    {
        val configs = matchingSections("^handler_[a-zA-Z_0-9]+$".r).
                      map(new HandlerConfig(this, _))
        Map.empty[String, HandlerConfig] ++ configs.map(cfg => (cfg.name, cfg))
    }

    private def validateHandlers: Option[String] = None
}

private[avsl] trait ConfiguredSection
{
    val config: AVSLConfiguration
    val section: Section

    protected def requiredString(option: String): String =
    {
        config.get(section.name, option) match
        {
            case Some(value) =>
                value
            case None =>
                throw new AVSLMissingRequiredOptionException(section.name,
                                                             option)
        }
    }

    protected def configuredLevel: LogLevel =
    {
        config.get(section.name, AVSLConfiguration.LevelKeyword) match
        {
            case Some(value) =>
                LogLevel.fromString(value) match
                {
                    case Some(level) =>
                        level
                    case None =>
                        throw new AVSLConfigSectionException(
                            section.name, "Bad log level: \"" + value + "\""
                        )
                }

            case None =>
                throw new AVSLMissingRequiredOptionException(
                    section.name, AVSLConfiguration.LevelKeyword
                )
        }
    }

    protected def classOption(keyword: String,
                              aliases: Map[String,Class[_]]): Option[Class[_]] =
        section.options.get(keyword) match
        {
            case Some(name) if (aliases.keySet.contains(name)) =>
                Some(aliases(name))
            case Some(name) =>
                Some(Class.forName(name))
            case None =>
                None
        }

    protected def argMap(filterOp: String => Boolean): Map[String, String] =
        Map.empty[String, String] ++
        section.options.keys.filter(filterOp).map(k => (k, section.options(k)))
}

private[avsl] class LoggerConfig(val config: AVSLConfiguration,
                                 val section: Section)
extends ConfiguredSection
{
    val name = section.name.replace(AVSLConfiguration.LoggerPrefix, "")
    val pattern = if (name == "root") "" else requiredString("pattern")
    val level = configuredLevel
    val handlerNames = section.options.getOrElse("handlers", "").
                               split("""[\s,]+""")

    if (name == "")
        throw new AVSLConfigSectionException(section.name,
                                             "Bad logger section name: \"" +
                                             section.name + "\"")
}

private[avsl] class HandlerConfig(val config: AVSLConfiguration,
                                  val section: Section)
extends ConfiguredSection
{
    val ClassAliases = Map("DefaultHandler" -> classOf[ConsoleHandler],
                           "ConsoleHandler" -> classOf[ConsoleHandler],
                           "FileHandler"    -> classOf[FileHandler])
    val DefaultHandlerClass = classOf[ConsoleHandler]

    val name = section.name.replace(AVSLConfiguration.HandlerPrefix, "")
    val level = configuredLevel
    val args = argMap(! isReserved(_))
    val handlerClass =
        classOption("class", ClassAliases).getOrElse(DefaultHandlerClass)

    if (name == "")
        throw new AVSLConfigSectionException(section.name,
                                             "Bad handler section name: \"" +
                                             section.name + "\"")

    private def isReserved(s: String): Boolean =
        (s == "class") ||
        (s == "formatter") ||
        (s == AVSLConfiguration.LevelKeyword) ||
        (s.startsWith(AVSLConfiguration.HandlerPrefix))

}

private[avsl] class FormatterConfig(val config: AVSLConfiguration,
                                    val section: Section)
extends ConfiguredSection
{
    val ClassAliases = Map("DefaultFormatter" -> classOf[SimpleFormatter])
    val DefaultFormatterClass = classOf[SimpleFormatter]

    val name = section.name.replace(AVSLConfiguration.FormatterPrefix, "")
    val args = argMap(! isReserved(_))

    val formatterClass = 
        classOption("class", ClassAliases).getOrElse(DefaultFormatterClass)

    if (name == "")
        throw new AVSLConfigSectionException(section.name,
                                             "Bad handler section name: \"" +
                                             section.name + "\"")

    private def isReserved(s: String): Boolean =
        (s == "class") ||
        (s.startsWith(AVSLConfiguration.FormatterPrefix))
}

/*---------------------------------------------------------------------------*\
                             Companion Object
\*---------------------------------------------------------------------------*/

//private[avsl]
object AVSLConfiguration
{
    val PropertyName    = "org.clapper.avsl.config"
    val EnvVariable     = "AVSL_CONFIG"
    val DefaultName     = "avsl.conf"
    val LevelKeyword    = "level"
    val HandlerPrefix   = "handler_"
    val LoggerPrefix    = "logger_"
    val FormatterPrefix = "formatter_"

    private val SearchPath = List(sysProperty _, 
                                  envVariable _,
                                  resource _)

    def load: Option[AVSLConfiguration] =
    {
        find match
        {
            case None      => None
            case Some(url) => Some(new AVSLConfiguration(url))
        }
    }

    private def find: Option[URL] =
    {
        def search(functions: List[() => Option[URL]]): Option[URL] =
        {
            functions match
            {
                case function :: Nil =>
                    function()

                case function :: tail =>
                    function() match
                    {
                        case None      => search(tail)
                        case Some(url) => Some(url)
                    }

                case Nil =>
                    None
            }
        }

        search(SearchPath)
    }

    private def resource(): Option[URL] =
    {
        this.getClass.getClassLoader.getResource("avsl.conf") match
        {
            case null => None
            case url  => Some(url)
        }
    }

    private def envVariable(): Option[URL] =
        urlString("Environment variable " + EnvVariable, 
                   System.getenv(EnvVariable))

    private def sysProperty(): Option[URL] =
        urlString("-D" + PropertyName, System.getProperty(PropertyName))

    private def urlString(label: String, getValue: => String): Option[URL] =
    {
        val s = getValue
        if ((s == null) || (s.trim.length == 0))
            None
        else
            urlOrFile(label, s)
    }

    private def urlOrFile(label: String, s: String): Option[URL] =
    {
        try
        {
            Some(new URL(s))
        }

        catch
        {
            case _: MalformedURLException =>
                val f = new File(s)
                if (! f.exists)
                {
                    println("Warning: " + label + " specifies nonexistent " +
                            "file \"" + f.getPath + "\"")
                    None
                }
                else
                    Some(f.toURI.toURL)
        }
    }
}

