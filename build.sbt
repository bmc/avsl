// ---------------------------------------------------------------------------
// Basic settings

name := "avsl"
organization := "org.clapper"
version := "1.0.17"
licenses := Seq("BSD" -> url("http://software.clapper.org/avsl/license.html"))
homepage := Some(url("http://software.clapper.org/avsl/"))
description := "A Very Simple Logger"
scalaVersion := "2.10.6"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

crossScalaVersions := Seq("2.10.6", "2.11.11", "2.12.3")

bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("logging", "scala")

// ---------------------------------------------------------------------------
// Other dependendencies

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"      % "3.0.1" % "test",
  "org.clapper"   %% "grizzled-scala" % "4.4.1",
  "com.sun.mail"   % "javax.mail"     % "1.6.0",
  "org.slf4j"      % "slf4j-api"      % "1.7.25"
)

// ---------------------------------------------------------------------------
// Publishing criteria

// Don't set publishTo. The Bintray plugin does that automatically.

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:bmc/avsl.git/</url>
    <connection>scm:git:git@github.com:bmc/avsl.git</connection>
  </scm>
  <developers>
    <developer>
      <id>bmc</id>
      <name>Brian Clapper</name>
      <url>http://www.clapper.org/bmc</url>
    </developer>
  </developers>
)
