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

package org.clapper.avsl.handler

import org.clapper.avsl.{LogLevel, LogMessage}
import org.clapper.avsl.formatter.Formatter
import org.clapper.avsl.config.ConfiguredArguments

import grizzled.string.implicits._

import java.io.{File, FileWriter, PrintWriter}
import java.util.Date

/**
 * Simple file handler that appends to a file. `args` must contain:
 *
 * - `path`: Pathname of file
 *
 * `args` may also contain:
 *
 * - `append`: "true" (as a string) to append, "false" to overwrite. Default:
 *   "false".
 */
class FileHandler(args: ConfiguredArguments,
                  formatter: Formatter,
                  val level: LogLevel)
extends Handler
{
    val file = new File(args("path"))
    val append: Boolean = args.getOrElse("append", "false").toString

    private val writer = new PrintWriter(new FileWriter(file, append), true)

    def log(logMessage: LogMessage) =
        writer.synchronized {writer.println(formatter.format(logMessage))}
}
