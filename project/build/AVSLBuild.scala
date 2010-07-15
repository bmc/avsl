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

import sbt._

import scala.io.Source

import java.io.File

/**
 * To build AVSL via SBT.
 */
class AVSLProject(info: ProjectInfo)
extends DefaultProject(info)
with posterous.Publish
{
    /* ---------------------------------------------------------------------- *\
                         Compiler and SBT Options
    \* ---------------------------------------------------------------------- */

    override def compileOptions = Unchecked :: super.compileOptions.toList
    override def parallelExecution = true // why not?

    /* ---------------------------------------------------------------------- *\
                             Various settings
    \* ---------------------------------------------------------------------- */

    val sourceDocsDir = "src" / "docs"
    val targetDocsDir = "target" / "doc"

    /* ---------------------------------------------------------------------- *\
                       Managed External Dependencies
    \* ---------------------------------------------------------------------- */

    val scalaToolsRepo = "Scala-Tools Maven Repository" at 
        "http://scala-tools.org/repo-releases/"

    val newReleaseToolsRepository = "Scala Tools Repository" at
        "http://nexus.scala-tools.org/content/repositories/snapshots/"
/*
    val ScalaTestVersion = buildScalaVersion
*/
    // Until a new build of ScalaTest is released.
    val ScalaTestVersion = "2.8.0.RC7"

    val scalatest = "org.scalatest" % "scalatest" %
        ("1.2-for-scala-" + ScalaTestVersion + "-SNAPSHOT")

    val slf4j = "org.slf4j" % "slf4j-api" % "1.6.0"

    val javaNetRepo = "Java.net Repository for Maven" at
        "http://download.java.net/maven/2"

    val javamailSMTP = "javax.mail" % "mail" % "1.4.3"

    val orgClapperRepo = "clapper.org Maven Repository" at
        "http://maven.clapper.org"
    val grizzled = "org.clapper" %% "grizzled-scala" % "0.7.3"

    /* ---------------------------------------------------------------------- *\
                                Publishing
    \* ---------------------------------------------------------------------- */

    lazy val home = Path.fileProperty("user.home")
    lazy val publishTo = Resolver.sftp("clapper.org Maven Repo",
                                       "maven.clapper.org",
                                       "/var/www/maven.clapper.org/html") as
                         ("bmc", (home / ".ssh" / "id_dsa").asFile)

    override def managedStyle = ManagedStyle.Maven

    /* ---------------------------------------------------------------------- *\
                         Custom tasks and actions
    \* ---------------------------------------------------------------------- */

    /* ---------------------------------------------------------------------- *\
                          Private Helper Methods
    \* ---------------------------------------------------------------------- */

}
