package org.clapper.avsl.handler

import org.clapper.avsl.formatter.Formatter
import org.clapper.avsl.config.ConfiguredArguments
import org.clapper.avsl.{LogLevel, LogMessage}

/**
  * Simple handler discards messages.
  */
class NullHandler(val level: LogLevel, val formatter: Formatter)
extends Handler {
  def this (args: ConfiguredArguments,
            formatter: Formatter,
            level: LogLevel) = this(level, formatter)

  def log(message: String, logMessage: LogMessage): Unit = {}
}
