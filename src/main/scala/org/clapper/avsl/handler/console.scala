package org.clapper.avsl.handler

import org.clapper.avsl.formatter.Formatter
import org.clapper.avsl.config.ConfiguredArguments
import org.clapper.avsl.{LogLevel, LogMessage}

/**
  * Simple file handler that logs to standard output.
  */
class ConsoleHandler(args: ConfiguredArguments,
                     val formatter: Formatter,
                     val level: LogLevel)
extends Handler {
  def log(message: String, logMessage: LogMessage) = Console.println(message)
}
