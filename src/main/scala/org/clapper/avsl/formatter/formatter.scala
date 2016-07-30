package org.clapper.avsl.formatter

import org.clapper.avsl.LogMessage

/**
  * Basic interface for a message formatter.
  */
trait Formatter {
  /** Format a log message, returning the formatted string.
    *
    * @param logMessage the log message to format
    *
    * @return the formatted message
    */
    def format(logMessage: LogMessage): String
}
