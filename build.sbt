// ---------------------------------------------------------------------------
// Basic settings

name := "avsl"
organization := "org.clapper"
version := "1.1.0"
licenses := Seq(
  "Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
)
homepage := Some(url("http://software.clapper.org/avsl/"))
description := "A Very Simple Logger"
scalaVersion := "2.13.0"

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0")

bintrayPackageLabels := Seq("library", "logging", "scala")

// ---------------------------------------------------------------------------
// Other dependendencies

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"      % "3.0.8" % Test,
  "org.clapper"   %% "grizzled-scala" % "4.9.3",
  "com.sun.mail"   % "javax.mail"     % "1.6.2",
  "org.slf4j"      % "slf4j-api"      % "1.7.26"
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
