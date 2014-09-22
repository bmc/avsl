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
import org.clapper.avsl.AVSLConfigException

import java.util.{Date, Properties};

import javax.activation.{DataHandler, DataSource, FileTypeMap}
import javax.mail.{Address, Message, MessagingException, Session, Transport}
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage,
                            MimeMultipart}

import scala.util.Try

/**
  * Handler that emails each message, separate, to a set of recipients.
  * `args` must contain:
  *
  * - `sender`: Email address of sender.
  * - `recipients`: comma-separated list of email addresses to receive log
  *    messages.
  *
  * `args` may also contain:
  *
  * - `smtp.server`: hostname of SMTP server. Defaults to "localhost".
  * - `smtp.port`: integer port on which SMTP server accepts messages. Defaults
  *   to 25.
  * - `subject`: Subject of message. Subject may contain "%l" for the level
  *   name. Defaults to: %l message
  */
class EmailHandler(args: ConfiguredArguments,
                   val formatter: Formatter,
                   val level: LogLevel)
extends Handler {
  // Pull the argument values.

  val sender = args.get("sender").
                    map { new InternetAddress(_) }.
                    getOrElse {
    throw new AVSLConfigException("Missing 'sender' for EmailHandler.")
  }

  val recipients = args.get("recipients").map {
    InternetAddress.parse(_, true).map { _.asInstanceOf[Address] }
  }.
  getOrElse {
    throw new AVSLConfigException("No recipients specified " +
                                  "for email handler.")
  }

  val smtpServer = args.getOrElse("smtp.server", "localhost")

  val smtpPort = args.get("smtp.port").map { sPort =>
    Try {
      sPort.toInt
    }.
    recover { case ex: NumberFormatException =>
      throw new AVSLConfigException("Bad SMTP port: " + sPort)
    }.
    get
  }

  val subject = args.getOrElse("subject", "%l message")

  // Initialize the JavaMail properties.

  private lazy val props = new Properties
  props.put("mail.smtp.host", smtpServer)
  props.put("mail.smtp.allow8bitmime", "true")

  // Prepare the JavaMail session and transport.

  private lazy val session = Session.getDefaultInstance(props, null)
  private lazy val transport = session.getTransport("smtp")

  // ----------------------------------------------------------------------
  // Classes
  // ----------------------------------------------------------------------

  /** A Java Activation Framework (JAF) DataSource that reads from a
    * string. Why such a thing isn't included in the JAF API escapes my
    * meager cognitive abilities.
    */
  private class StringDataSource(s: String) extends DataSource {

    import java.io.{InputStream, OutputStream, IOException,
                    ByteArrayInputStream}

    val contentType = "text/plain"

    def getInputStream(): InputStream =
      new ByteArrayInputStream(s.getBytes)

    def getOutputStream(): OutputStream =
      throw new IOException("OutputStream not supported for string")

    def getContentType() = contentType

    def getName() = "body"
  }

  // ----------------------------------------------------------------------
  // Methods
  // ----------------------------------------------------------------------

  def log(message: String, logMessage: LogMessage) = {
    // Create a new message. (Isn't this fun?)

    val mailMessage = new MimeMessage(session)
    val body = new MimeMultipart

    val bodyPart = new MimeBodyPart
    bodyPart.setDataHandler(new DataHandler(new StringDataSource(message)))

    body.addBodyPart(bodyPart)

    mailMessage.setSender(sender)
    mailMessage.setFrom(sender)
    mailMessage.setRecipients(Message.RecipientType.TO, recipients)
    mailMessage.setSubject(subject.replaceAll("%l", logMessage.level.label))
    mailMessage.setContent(body)
    mailMessage.addHeaderLine("X-Mailer: " + this.getClass.getName)
    mailMessage.setSentDate(new Date)

    // Connect to the SMTP server and send the message.

    transport.connect
    Transport.send(mailMessage)
    transport.close
  }
}
