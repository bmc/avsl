// ---------------------------------------------------------------------------
// Basic settings

name := "avsl"

organization := "org.clapper"

version := "1.0"

licenses := Seq("BSD" -> url("http://software.clapper.org/avsl/license.html"))

homepage := Some(url("http://software.clapper.org/avsl/"))

description := "A Very Simple Logger"

scalaVersion := "2.10.0-M7"

// ---------------------------------------------------------------------------
// Additional compiler options and plugins

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

crossScalaVersions := Seq("2.10.0-M7")

seq(lsSettings :_*)

(LsKeys.tags in LsKeys.lsync) := Seq("logging", "logger", "simple")

(description in LsKeys.lsync) <<= description(d => d)

// ---------------------------------------------------------------------------
// Additional repositories

resolvers ++= Seq(
    "Java.net Maven 2 Repo" at "http://download.java.net/maven/2"
)

// ---------------------------------------------------------------------------
// ScalaTest dependendency

libraryDependencies <<= (scalaVersion, libraryDependencies) { (sv, deps) =>
    // Select ScalaTest version based on Scala version
    val scalatestVersionMap = Map(
      "2.8.0"     -> ("scalatest_2.8.0", "1.3.1.RC2"),
      "2.8.1"     -> ("scalatest_2.8.1", "1.7.1"),
      "2.8.2"     -> ("scalatest_2.8.2", "1.7.1"),
      "2.9.0"     -> ("scalatest_2.9.0", "1.7.1"),
      "2.9.0-1"   -> ("scalatest_2.9.0-1", "1.7.1"),
      "2.9.1"     -> ("scalatest_2.9.1", "1.7.1"),
      "2.9.1-1"   -> ("scalatest_2.9.1", "1.7.1"),
      "2.9.2"     -> ("scalatest_2.9.1", "1.7.1"),
      "2.10.0-M7" -> ("scalatest_2.10.0-M7", "1.9-2.10.0-M7-B1")
    )
    val (scalatestArtifact, scalatestVersion) = scalatestVersionMap.getOrElse(
        sv, error("Unsupported Scala version: " + scalaVersion)
    )
    deps :+ "org.scalatest" % scalatestArtifact % scalatestVersion % "test"
}

// ---------------------------------------------------------------------------
// Other dependendencies

libraryDependencies ++= Seq(
    "org.clapper" % "grizzled-scala_2.10" % "1.1.0",
    "javax.mail" % "mail" % "1.4.3",
    "org.slf4j" % "slf4j-api" % "1.7.1"
)

// ---------------------------------------------------------------------------
// Publishing criteria

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

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
