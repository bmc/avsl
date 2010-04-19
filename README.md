AVSL - A Very Simple Logger
===========================

## Overview

AVSL is a very simple logging framework. AVSL implements the [SLF4J][] API,
allowing applications to be written to the [SLF4J][] API. Because it
implements SLF4J, AVSL can easily be swapped for another SLF4J-compatible
logging framework (or the other way around), without any changes to the
calling application.

AVSL differs from other logging APIs, such as [Logback][], in several ways:

* It's simpler. AVSL is a very lightweight logging framework. It is
  intended to be used primarily in standalone program, not enterprise
  applications. (It may well work for your enterprise application, of course;
  but, if it doesn't, you can easily switch to something else.)
* It uses a simple INI-style configuration file, rather than an XML
  configuration file. (I hate XML configuration files.)
* In addition to being SLF4J-compliant, it provides a Scala-friendly
  front end, modeled after the logging API in the [Lift][] framework.

For more information, please see the [AVSL web page][].

## Copyright and License

AVSL is copyright &copy; 2010 [Brian M. Clapper][] and is released under
a BSD license. See the accompanying license file for details.

[Logback]: http://logback.qos.ch/
[Scala]: http://www.scala-lang.org/
[Lift]: http://liftweb.net/
[AVSL web page]: http://bmc.github.com/avsl/
[Brian M. Clapper]: mailto:bmc@clapper.org
