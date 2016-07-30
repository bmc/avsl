package org.clapper.avsl.formatter

import org.clapper.avsl.LogMessage
import org.clapper.avsl.config.ConfiguredArguments

class NullFormatter extends Formatter {
  def this(args: ConfiguredArguments) = this()

  def format(logMessage: LogMessage): String = ""
}
