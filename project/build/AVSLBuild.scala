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
 * To build Novus via SBT.
 */
class NovusProject(info: ProjectInfo)
extends DefaultProject(info)
with posterous.Publish
{
    /* ---------------------------------------------------------------------- *\
                         Compiler and SBT Options
    \* ---------------------------------------------------------------------- */

    override def compileOptions = Unchecked :: super.compileOptions.toList
    override def parallelExecution = true // why not?

    // Specialization causes problems with inner classes. Disabling it, for
    // now, allows the tests to run. It can be re-enabled when compiler
    // bugs are fixed.
    override def testCompileOptions = super.testCompileOptions ++
        Seq(CompileOption("-no-specialization"))

    // Disable cross-paths, since we're only building under one version.
    // This simplifies publishing and importing. See
    // http://groups.google.com/group/simple-build-tool/browse_thread/thread/973b5a2956b5ecbe

    override def disableCrossPaths = true

    /* ---------------------------------------------------------------------- *\
                             Various settings
    \* ---------------------------------------------------------------------- */

    val sourceDocsDir = "src" / "docs"
    val targetDocsDir = "target" / "doc"
    val scalaVersionDir = "scala-" + buildScalaVersion

    /* ---------------------------------------------------------------------- *\
                       Managed External Dependencies

               NOTE: Additional dependencies are declared in
         project/plugins/Plugins.scala. (Declaring them there allows them
                       to be imported in this file.)
    \* ---------------------------------------------------------------------- */

    val scalaToolsRepo = "Scala-Tools Maven Repository" at 
        "http://scala-tools.org/repo-releases/"

    val newReleaseToolsRepository = "Scala Tools Repository" at
        "http://nexus.scala-tools.org/content/repositories/snapshots/"
    val scalatest = "org.scalatest" % "scalatest" %
        "1.0.1-for-scala-2.8.0.RC1-SNAPSHOT"

    val slf4j = "org.slf4j" % "slf4j-api" % "1.5.11"

    val javaNetRepo = "Java.net Repository for Maven" at
        "http://download.java.net/maven/2"

    val javamailSMTP = "javax.mail" % "mail" % "1.4.3"

    val orgClapperRepo = "clapper.org Maven Repository" at
        "http://maven.clapper.org"
    val grizzled = "org.clapper" % "grizzled-scala" % "0.5"

    /* ---------------------------------------------------------------------- *\
                                Publishing
    \* ---------------------------------------------------------------------- */

    // "publish" will prompt (via a Swing pop-up) for the username and
    // password.
    lazy val publishTo = Resolver.sftp("clapper.org Maven Repo",
                                       "maven.clapper.org",
                                       "/var/www/maven.clapper.org/html")

    override def managedStyle = ManagedStyle.Maven

    /* ---------------------------------------------------------------------- *\
                         Custom tasks and actions
    \* ---------------------------------------------------------------------- */

    /* ---------------------------------------------------------------------- *\
                          Private Helper Methods
    \* ---------------------------------------------------------------------- */

}
