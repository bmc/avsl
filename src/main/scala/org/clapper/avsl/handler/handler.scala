package org.clapper.avsl.handler

import org.clapper.avsl.{LogLevel, LogMessage}
import org.clapper.avsl.formatter.Formatter

import java.util.Date

/**
  * Basic interface for a handler that dispatches log messages.
  */
trait Handler {
  /** The log level associated with the handler.
    */
  val level: LogLevel

  /** The formatter associated with the handler.
    */
  val formatter: Formatter

  /** Log a message, wherever the handler logs its output. The method will
    * only be called if the message's level is below or equal to the level
    * associated with the handler.
    *
    * @param message    the already-formatted message to log
    * @param logMessage the raw log message, in case the pieces are needed
    */
  def log(message: String, logMessage: LogMessage): Unit
}
