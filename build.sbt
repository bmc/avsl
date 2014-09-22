import ls.Plugin.LsKeys

// ---------------------------------------------------------------------------
// Basic settings

name := "avsl"

organization := "org.clapper"

version := "1.0.2"

licenses := Seq("BSD" -> url("http://software.clapper.org/avsl/license.html"))

homepage := Some(url("http://software.clapper.org/avsl/"))

description := "A Very Simple Logger"

scalaVersion := "2.10.4"

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

crossScalaVersions := Seq("2.10.4", "2.11.2")

seq(lsSettings :_*)

(LsKeys.tags in LsKeys.lsync) := Seq("logging", "logger", "simple")

(description in LsKeys.lsync) <<= description(d => d)

bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := (
  LsKeys.tags in LsKeys.lsync
).value

externalResolvers in LsKeys.lsync := (resolvers in bintray.Keys.bintray).value

// ---------------------------------------------------------------------------
// Additional repositories

resolvers ++= Seq(
    "Java.net Maven 2 Repo" at "https://maven-repository.dev.java.net/nonav"
)

// ---------------------------------------------------------------------------
// Other dependendencies

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.3" % "test",
  "org.clapper" %% "grizzled-scala" % "1.2",
  "javax.mail" % "mail" % "1.4.3",
  "org.slf4j" % "slf4j-api" % "1.7.1"
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
