AVSL - A Very Simple Logger
===========================

[![Build Status](https://travis-ci.org/bmc/avsl.svg?branch=master)](https://travis-ci.org/bmc/avsl)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.clapper/avsl_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.clapper/avsl_2.11)

## Overview

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
* AVSL does not monitor and reload its configuration file.
* AVSL does not implement the SLF4J marker or MDC capabilities.
* AVSL does not wrap other logging frameworks.
* AVSL does not let programmers define their own log levels.

In short, AVSL is a perfectly serviceable, simple logging framework that can
easily be swapped out for something with more features.

For more information, please see the [AVSL web page][].

**NOTE**: Starting with version 1.1.0, AVSL no longer supports Scala 2.10.
For information on which AVSL versions support which Scala versions, see
the [AVSL web page][].

[pre-scala-2.10-fixes]: https://github.com/bmc/avsl/tree/pre-scala-2.10-fixes

## Copyright and License

AVSL is copyright &copy; 2010-2019 [Brian M. Clapper][].

- Starting with version 1.1.0, AVSL is released under the 
  [Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- Versions prior to 1.1.0 were released under the
  [3-Clause BSD License](https://opensource.org/licenses/BSD-3-Clause). The
  BSD license still applies to these older versions.

[Logback]: http://logback.qos.ch/
[Scala]: http://www.scala-lang.org/
[Lift]: http://liftweb.net/
[AVSL web page]: http://bmc.github.com/avsl/
[Brian M. Clapper]: mailto:bmc@clapper.org
[SLF4J]: http://slf4j.org/
[Python logging module]: http://docs.python.org/library/logging.html
[Grizzled-SLF4J]: http://bmc.github.com/grizzled-slf4j/
