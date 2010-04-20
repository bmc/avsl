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

import grizzled.config.Configuration
import scala.io.Source
import scala.collection.mutable.{Map => MutableMap, Set => MutableSet}
import java.net.{MalformedURLException, URL}
import java.io.File

/**
 * The configuration handler.
 */
private[avsl] class AVSLConfiguration(source: Source) extends Configuration
{
    load(source)

    
}

private[avsl] class LoggerConfig(val name: String, val level: LogLevel)
private[avsl] class HandlerConfig(val name: String,
                                  val className: String,
                                  val level: LogLevel)

private[avsl] object AVSLConfiguration
{
    val PropertyName = "org.clapper.avsl.config"
    val EnvVariable  = "AVSL_CONFIG"
    val DefaultName  = "avsl.conf"

    private val SearchPath = List(sysProperty _, 
                                  envVariable _,
                                  resource _)

    def load: Option[AVSLConfiguration] =
    {
        find match
        {
            case None      => None
            case Some(url) => Some(new AVSLConfiguration(Source.fromURL(url)))
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

