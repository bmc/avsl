---
title: AVSL, A Very Simple Logger
subtitle: Because, sometimes, simpler is better.
layout: withTOC
---

[![Build Status](https://travis-ci.org/bmc/avsl.svg?branch=master)](https://travis-ci.org/bmc/avsl)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.clapper/avsl_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.clapper/avsl_2.11)

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

AVSL is published to my
[Bintray Maven repository](https://bintray.com/bmc/maven), which is
automatically linked to Bintray's [JCenter](https://bintray.com/bintray/jcenter)
repository. (From JCenter, it's eventually pushed to the
[Maven Central Repository][]).

* Versions 1.0.12 through 1.0.16 support Scala 2.10, 2.11 and 2.12.
* Version 1.0.11 supports Scala 2.10, 2.11 and 2.12.0-M5
* Version 1.0.9 and 1.1.10 support Scala 2.10, 2.11 and 2.12.0-M4
* Versions 1.0.1 through 1.0.8 supports Scala 2.10
* Version 1.0 supports Scala 2.10.0-M7
* Version 0.4 supports Scala 2.9.2, 2.9.1-1, 2.9.1, 2.9.0-1, 2.9.0, 2.8.2,
  2.8.1 and 2.8.0.

### Installing for Maven

If you're using [Maven][], just give it the artifact, and Maven will do the rest:

* Group ID: `clapper.org`
* Artifact ID: `avsl_2.9.2` or `avsl_2.10`/`avsl_2.11`/`avsl_2.12`
* Version: `0.4` or `1.0.10`
* Type: `jar`

For example:

    <dependency>
      <groupId>org.clapper</groupId>
      <artifactId>avsl_2.11</artifactId>
      <version>1.0.16</version>
    </dependency>

For more information on using Maven and Scala, see Josh Suereth's
[Scala Maven Guide][].

### Using with SBT

Add the following to your SBT build:

    libraryDependencies += "org.clapper" %% "avsl" % "1.0.16"

## Source Code Repository

The source code for AVSL is maintained on [GitHub][]. To clone the
repository, run this command:

    $ git clone git://github.com/bmc/avsl.git

## Building from Source

Building the library requires [SBT][] 0.13.x, but you don't need to
download it, as the repo has a copy of Lightbend Activator. To build,
just run:

    bin/activator +compile +test +package

The resulting jar file will be in the top-level `target` directory.

## Documentation

Please consult the [User's Guide][] for details on how to use and configure
AVSL.

## Change log

The change log for all releases is [here][changelog].

## Author

Brian M. Clapper, [bmc@clapper.org][]

## Copyright and License

AVSL is copyright &copy; 2010-2014 Brian M. Clapper and is released under a
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
[Scala Maven Guide]: http://www.scala-lang.org/node/345
[changelog]: https://github.com/bmc/avsl/blob/master/CHANGELOG.md
[Maven central repository]: http://search.maven.org/
[ls.implicit.ly]: http://ls.implicit.ly
