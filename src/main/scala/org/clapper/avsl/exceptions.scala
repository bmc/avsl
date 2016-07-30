/**
  * AVSL exception classes.
  */
package org.clapper.avsl

class AVSLException(message: String) extends Exception(message)

class AVSLConfigException(message: String)
extends AVSLException(message)

class AVSLConfigSectionException(section: String, message: String)
extends AVSLConfigException("Configuration error in section [" + section +
                            "]: " + message)

class AVSLMissingRequiredOptionException(section: String, option: String)
extends AVSLConfigSectionException(section,
                                   "Missing required option \"" + option + "\"")
