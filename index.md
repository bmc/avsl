---
title: AVSL, A Very Simple Logger
subtitle: Because, sometimes, simpler is better.
layout: withTOC
---

## Introduction

AVSL is *a very simple logger*, written in [Scala][]. AVSL implements the
[Simple Logging Facade for Java][SLF4J] (SLF4J) API, allowing applications
to be written to the [SLF4J][] API. (This, of course, includes Java
applications.) Because it implements SLF4J, AVSL can easily be swapped for
another SLF4J-compatible logging framework (or the other way around),
without any changes to the calling application. Also, because it supports
SLF4J, AVSL can be used in conjunction with Scala SLF4J wrappers, such as
[Grizzled-SLF4J][].

"AVSL" stands for "A Very Simple Logger", and AVSL strives for simplicity
in several ways.

* AVSL is simple to configure, using a non-XML, INI-style configuration
  file that's reminiscent of the [Python logging module][]'s configuration.
  This simpler configuration file is easier to read and edit than the XML
  configuration files used by logging frameworks such as [Logback][].
  (Since I dislike XML configuration files, this is big win for me.)
* AVSL is a lightweight logging framework. It is intended to be used
  primarily in standalone programs, not enterprise applications. It may
  work fine for your enterprise application, of course; but, if it doesn't,
  you can easily switch to something else.
* The default message formatter uses a simpler, more compact syntax than
  Java's `SimpleDateFormat`, relying on `strftime`-like escapes.
* You can specify the configuration file via an environment variable
  (`AVSL_CONFIG`) or a Java system property. If neither is present, AVSL
  looks for resource `avsl.conf` somewhere in the classpath.
* The built-in logging handlers are thread-safe.
* AVSL does not monitor and reload its configuration file.
* AVSL does not implement the SLF4J marker or MDC capabilities.
* AVSL does not wrap other logging frameworks.
* AVSL does not let programmers define their own log levels.

In short, AVSL is a perfectly serviceable, simple logging framework that can
easily be swapped out for something with more features.

## Installation

The easiest way to install AVSL is to download a pre-compiled jar from the
[Scala Tools Maven repository][]. However, you can also get certain build
tools to download it for you automatically.

### Installing for Maven

If you're using [Maven][], you can simply tell Maven to get AVSL from the
[Scala Tools Maven repository][]. The relevant pieces of information are:

* Group ID: `clapper.org`
* Artifact ID: `avsl_2.9.0-1`
* Version: `0.3.3`
* Type: `jar`
* Repository: `http://www.scala-tools.org/repo-releases/`

For example:

    <repositories>
      <repository>
        <id>scala-tools.org</id>
          <name>Scala-tools Maven2 Repository</name>
          <url>http://scala-tools.org/repo-releases</url>
      </repository>
    </repositories>

    <dependency>
      <groupId>org.clapper</groupId>
      <artifactId>avsl_2.9.0-1</artifactId>
      <version>0.3.3</version>
    </dependency>

Version 0.3.3 is available for Scala 2.9.0-1, 2.9.0, 2.8.1 and 2.8.0.

For more information on using Maven and Scala, see Josh Suereth's
[Scala Maven Guide][].

### Using with SBT

#### 0.7.x

If you're using [SBT][] 0.7.x to compile your code, you can place the
following line in your project file (i.e., the Scala file in your
`project/build/` directory):

    val javaNetRepo = "Java.net Maven 2 Repo" at
        "http://download.java.net/maven/2"
    val avsl = "org.clapper" %% "avsl" % "0.3.3"

#### 0.10.x

If you're using [SBT][] 0.10.x to compile your code, you can use the
following line in your `build.sbt` file (for Quick Configuration). If
you're using an SBT 0.10.x Full Configuration, you're obviously smart
enough to figure out what to do, on your own.

    resolvers += "Java.net Maven 2 Repo" at "http://download.java.net/maven/2"
    libraryDependencies += Seq(
        "org.clapper" %% "avsl" % "0.3.3",


**NOTES**

1. You *must* specify the Java.net and `ScalaToolsSnapshots` repositories,
   Even though those additional repositories are in the published AVSL
   Maven `pom.xml`, SBT will not read them. Under the covers, SBT uses
   [Apache Ivy][] for dependency management, and Ivy doesn't extract
   repositories from Maven POM files. If you don't explicitly specify the
   additional repositories listed above, `sbt update` will fail. See
   [Library Management Maven/Ivy section][] in the [SBT Manual][] for
   details. Also see this [email thread][SBT-repo-email-thread]. Depending
   on your circumstances, you may also need to specify the dependent
   repositories used by the [Grizzled Scala][] library.

## Source Code Repository

The source code for AVSL is maintained on [GitHub][]. To clone the
repository, run this command:

    git clone git://github.com/bmc/avsl.git

## Building from Source

Building the library requires [SBT][] 0.10.1 or better. Install SBT, as
described at the SBT web site. Then, assuming you have an `sbt` shell
script (or .BAT file, for Windows), run:

    sbt compile package

The resulting jar file will be in the top-level `target` directory.

## Documentation

Please consult the [User's Guide][] for details on how to use and configure
AVSL.

## Change log

The change log for all releases is [here][changelog].

## Author

Brian M. Clapper, [bmc@clapper.org][]

## Copyright and License

AVSL is copyright &copy; 2010 Brian M. Clapper and is released under a
[BSD License][].

## Patches

I gladly accept patches from their original authors. Feel free to email
patches to me or to fork the [GitHub repository][] and send me a pull
request. Along with any patch you send:

* Please state that the patch is your original work.
* Please indicate that you license the work to the AVSL project
  under a [BSD License][].

[User's Guide]: users-guide.html
[BSD License]: license.html
[GitHub repository]: http://github.com/bmc/avsl
[Grizzled-SLF4J]: http://software.clapper.org/grizzled-slf4j/
[GitHub]: http://github.com/bmc/
[downloads area]: http://github.com/bmc/avsl/downloads
[Maven]: http://maven.apache.org/
[bmc@clapper.org]: mailto:bmc@clapper.org
[Scala]: http://www.scala-lang.org/
[Python logging module]: http://docs.python.org/library/logging.html
[SLF4J]: http://slf4j.org/
[Logback]: http://logback.qos.ch/
[Grizzled Scala]: http://software.clapper.org/grizzled-scala/
[SBT]: http://code.google.com/p/simple-build-tool
[strftime]: http://www.opengroup.org/onlinepubs/007908799/xsh/strftime.html
[call-by-name]: http://eed3si9n.com/scala-and-evaluation-strategy
[API documentation]: api
[RFC822]: http://www.ietf.org/rfc/rfc822.txt
[JavaMail API]: http://java.sun.com/products/javamail/
[SBT cross-building]: http://code.google.com/p/simple-build-tool/wiki/CrossBuild
[Apache Ivy]: http://ant.apache.org/ivy/
[Library Management Maven/Ivy section]: http://code.google.com/p/simple-build-tool/wiki/LibraryManagement#Maven/Ivy
[SBT Manual]: http://code.google.com/p/simple-build-tool/wiki/DocumentationHome
[SBT-repo-email-thread]: http://groups.google.com/group/simple-build-tool/browse_thread/thread/470bba921252a167
[Scala Tools Maven repository]: http://www.scala-tools.org/repo-releases/
[Scala Maven Guide]: http://www.scala-lang.org/node/345
[changelog]: CHANGELOG.html
