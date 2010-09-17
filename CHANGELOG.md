---
title: License for AVSL
layout: default
---

Version 0.3:

* Now published to the [Scala Tools Maven repository][], which [SBT][]
  includes by default. Thus, if you're using SBT, it's longer necessary to
  specify a custom repository to find this artifact.

[Scala Tools Nexus]: http://www.scala-tools.org/repo-releases/
[SBT]: http://code.google.com/p/simple-build-tool/

Version 0.2.5:

* Updated to released 1.2 version of [ScalaTest][].

[ScalaTest]: http://scalatest.org/

Version 0.2.4:

* Now compiles with Scala 2.8.0.final *only*.
* Updated to version 0.7.3 of [Grizzled Scala][].

[Grizzled Scala]: http://bmc.github.com/grizzled-scala/

Version 0.2.3:

* Now compiles with Scala 2.8.0.RC5, as well as RC3. Dropped support for RC2.
* Updated to version 0.7.2 of [Grizzled Scala][].

[Grizzled Scala]: http://bmc.github.com/grizzled-scala/

Version 0.2.2:

* Configuration logic now installs a default handler and formatter when
  creating a "fake" root node. This strategy avoids a nasty exception if
  an empty (or incorrect) configuration file is passed to AVSL. Now, if
  that happens, AVSL just silently configures itself to do nothing.
* Updated to [Grizzled Scala][] version 0.7.
* Updated to build with Scala 2.8.0.RC3. Dropped support for RC1.
* Updated to [SLF4J][] 1.6.
* Updated to [Simple Build Tool][] 0.7.4.

[SLF4J]: http://slf4j.org/
[Simple Build Tool]: http://code.google.com/p/simple-build-tool
[Grizzled Scala]: http://bmc.github.com/grizzled-scala/

Version 0.2.1:

* Updated to build against Scala 2.8.0.RC2.
* Maven artifact now includes Scala version number.
* Re-enabled specialization compilation for ScalaTest testers, since
  inner-class specialization compilation bugs appear to be fixed.

Version 0.2:

* Added a simple `EmailHandler` class. Using it requires the presence of
  the [JavaMail API][] `mail.jar` at runtime.
* Added some missing thread synchronization when retrieving handlers and
  formatters.

[JavaMail API]: http://java.sun.com/products/javamail/

Version 0.1:

AVSL is *a very simple logger*, written in [Scala][]. AVSL implements the
Simple Logging Facade for Java ([SLF4J][]) API, allowing applications to be
written to SLF4J, for portability across logging frameworks. (This, of
course, includes Java applications.) Because it implements SLF4J, AVSL can
easily be swapped for another SLF4J-compatible logging framework (or the
other way around), without any changes to the calling application. Also,
because it supports SLF4J, AVSL can be used in conjunction with Scala SLF4J
wrappers, such as [Grizzled-SLF4J][].

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

[Logback]: http://logback.qos.ch/
[Scala]: http://www.scala-lang.org/
[AVSL web page]: http://bmc.github.com/avsl/
[Brian M. Clapper]: mailto:bmc@clapper.org
[SLF4J]: http://slf4j.org/
[Python logging module]: http://docs.python.org/library/logging.html
[Grizzled-SLF4J]: http://bmc.github.com/grizzled-slf4j/
