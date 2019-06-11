package org.clapper.avsl.handler

import org.clapper.avsl.{Logger, LogLevel, LogMessage}
import org.clapper.avsl.formatter.Formatter
import org.clapper.avsl.config.ConfiguredArguments

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
                  val formatter: Formatter,
                  val level: LogLevel)
extends Handler {
  import grizzled.string.util

  private val logger = Logger("org.clapper.avsl.handler")
  val file = new File(args("path"))
  val sAppend = args.getOrElse("append", "false")
  val append = util.strToBoolean(sAppend) match {
    case Left(error) =>
      logger.error(s"Bad 'append' value ($sAppend) for FileHandler")
      false
    case Right(v) =>
      v
  }

  private val writer = new PrintWriter(new FileWriter(file, append), true)

  def log(message: String, logMessage: LogMessage): Unit = writer.println(message)
}
