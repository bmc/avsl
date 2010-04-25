AVSL - A Very Simple Logger
===========================

**WARNING: THIS PACKAGE IS STILL UNDER DEVELOPMENT! It is not fully
implemented yet. Don't expect it to work completely, and don't expect it
not to make off with your first-born child.**

**This notice will self-destruct when I decide AVSL is cooked all the way
to the center and the juices are no longer red.**

## Overview

AVSL is a very simple logging framework. AVSL implements the [SLF4J][] API,
allowing applications to be written to the [SLF4J][] API. Because it
implements SLF4J, AVSL can easily be swapped for another SLF4J-compatible
logging framework (or the other way around), without any changes to the
calling application.

AVSL differs from other logging APIs, such as [Logback][], in several ways:

* It's simpler. AVSL is a very lightweight logging framework. It is
  intended to be used primarily in standalone programs, not enterprise
  applications. (It may well work for your enterprise application, of course;
  but, if it doesn't, you can easily switch to something else.)
* It uses a simple INI-style configuration file, rather than an XML
  configuration file. (I hate XML configuration files.)
* The default formatter uses a simpler, more compact syntax than Java's
 `SimpleDateFormat`, relying on `printf`-like escapes.
* The logger does *not* monitor and reload its configuration file.
* It does not implement the SLF4J marker or MDC capabilities.
* It allows you to specify the location of the configuration file via an
  environment variable or a Java system property. If neither of those is
  defined, AVSL looks in some standard places. (See the [User's Guide][]
  for details.)

For more information, please see the [AVSL web page][] and the
[User's Guide][].

## Copyright and License

AVSL is copyright &copy; 2010 [Brian M. Clapper][] and is released under
a BSD license. See the accompanying license file for details.

[Logback]: http://logback.qos.ch/
[Scala]: http://www.scala-lang.org/
[Lift]: http://liftweb.net/
[AVSL web page]: http://bmc.github.com/avsl/
[User's Guide]: http://bmc.github.com/avsl/users-guide.html
[Brian M. Clapper]: mailto:bmc@clapper.org
