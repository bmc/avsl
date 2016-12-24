// ---------------------------------------------------------------------------
// Basic settings

name := "avsl"
organization := "org.clapper"
version := "1.0.13"
licenses := Seq("BSD" -> url("http://software.clapper.org/avsl/license.html"))
homepage := Some(url("http://software.clapper.org/avsl/"))
description := "A Very Simple Logger"
scalaVersion := "2.10.6"
ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.1")

bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("logging", "scala")

// ---------------------------------------------------------------------------
// Additional repositories

resolvers ++= Seq(
    "Java.net Maven 2 Repo" at "https://maven-repository.dev.java.net/nonav"
)

// ---------------------------------------------------------------------------
// Other dependendencies

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest"      % "3.0.0" % "test",
  "org.clapper"   %% "grizzled-scala" % "4.2.0",
  "javax.mail"     % "mail"           % "1.4.3",
  "org.slf4j"      % "slf4j-api"      % "1.7.21"
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
