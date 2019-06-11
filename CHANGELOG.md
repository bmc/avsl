# Change Log for AVSL.

Version 1.1.0

* Now cross-compiles against Scala 2.13.0, Scala 2.12.x and Scala 2.11.x.
* Support for Scala 2.10 **has been dropped**.
* Bumped Grizzled Scala to latest version.
* Now licensed under the [Apache License, version 2.0](https://www.apache.org/licenses/LICENSE-2.0),
  instead of the [3-Clause BSD License](https://opensource.org/licenses/BSD-3-Clause).
  The previous license still applies to older versions.
* Removed the old Lightbend Activator, which is now moribund.

Version 1.0.18

* Bumped Grizzled Scala to latest version.
* Bumped Scala versions.
* Bumped ScalaTest version.
* Updated to SBT version 1.x.

Version 1.0.17

* Fixed typo in `build.sbt` that led to a bad dependency in the
  generated POM file, rendering version 1.0.16 unusable.

Version 1.0.16 **Broken: Don't use**

* Updated dependencies.
* Added [coursier](https://github.com/coursier/coursier) to build.

Version 1.0.15

* Updated to Grizzled Scala 4.4.1.

Version 1.0.14

* Updated to Grizzled Scala 4.3.0.
* Updated version of Lightbend Activator.
* Updated to ScalaTest 3.0.1.
* Updated version of SBT to 0.13.15.

Version 1.0.13:

* Updated 2.12 build to 2.12.1.
* Updated to Grizzled Scala 4.1.0

Version 1.0.12:

* Updated 2.12 build to 2.12.0-final.
* Updated to Grizzled Scala 3.1.0
* Updated to ScalaTest 3.0.0.

Version 1.0.11:

* Removed last vestiges of `ls`.
* Updated to Grizzled Scala 2.6.0.
* Updated to ScalaTest 3.3.0-RC4.
* Updated 2.12 build to 2.12.0-M5.
* Updated to `slf4j-api` 1.7.21.
* Changed all uses of `scala.collection.JavaConversions` to
  `scala.collection.JavaConverters`, since the former is deprecated in
  Scala 2.12.

Version 1.0.10:

* Updated to Grizzled Scala 2.4.0.

Version 1.0.9:

* Updated to Grizzled Scala 2.2.1.
* Updated to Scalatest 2.2.6.
* Now builds for Scala 2.12, as well as 2.10 and 2.11.
* Integrated Travis CI.

Version 1.0.8:

* Updated to Grizzled Scala 2.2.0.
* Updated version of Lightbend Activator.

Version 1.0.7:

* Updated to Grizzled Scala 2.1.0.

Version 1.0.6:

* Updated to Grizzled Scala 2.0.1.

Version 1.0.5:

* Updated to Grizzled Scala 1.6.0.
* Added `activator` script.

Version 1.0.4:

* Updated to Grizzled Scala 1.5.0. Updated slf4j-api version to 1.7.16.

Version 1.0.3:

* Updated to Grizzled Scala 1.4.0. Updated SBT version.

Version 1.0.2:

* Updated ScalaTest dependencies. Modified tests for new version of
  ScalaTest. Added new `EmailHandlerSpec` test.
* Compiled for Scala 2.11, as well as 2.10.
* Updated Grizzled Scala to latest version, requiring changes to how
  configuration is parsed.
* Updated SBT version to 0.13.5.
* Removed various code-smelly matches against `Option` types.

Version 1.0.1:

* Compiled and published for Scala 2.10.0-RC1.
* Upgraded ScalaTest to 2.0.

Version 1.0:

* Now supports Scala 2.10.0-M7, in the 1.0 release. Scala 2.9.x and
  earlier are supported in the 0.4.x release(s) going forward.
* Upgraded to SLF4J 1.7.1.

Version 0.4:

* Added Scala 2.9.2 to the set of cross-built versions.

Version 0.3.8:

* Added Scala 2.9.1-1 and Scala 2.8.2 to the list of cross-built versions.

Version 0.3.7:

* Converted to build with SBT 0.11.2.
* Added support for `ls.implicit.ly` metadata.
* Now publishes to `oss.sonatype.org` (and, thence, to the Maven central repo).
* Bumped [Grizzled Scala][] and [SLF4J][] version dependencies.

Version 0.3.6:

* Merged patched from [Erik Rozendaal](https://github.com/erikrozendaal):
  *Use slf4j's MessageFormatter correctly to format log messages with
  *arguments*.

Version 0.3.5:

* Cross-built for Scala 2.9.1, as well as the usual suspects.

Version 0.3.4:

* Converted code to conform with standard Scala coding style.

Version 0.3.3:

* Now builds against Scala 2.9.0.1, as well as Scala 2.9.0, 2.8.1 and 2.8.0.
* Converted to build with [SBT][] 0.10.1

Version 0.3.2:

* Now builds against Scala 2.9.0, as well as Scala 2.8.0 and 2.8.1.
* Updated to version 1.4.1 of [ScalaTest][] for Scala 2.9.0. (Still uses
  ScalaTest 1.3, for Scala 2.8).
* Updated to use [SBT][] 0.7.7.
* Updated to version 1.6.1 of of [SLF4J][].
* Updated to version 1.0.6 of the [Grizzled Scala][] library.

[ScalaTest]: http://www.scalatest.org/
[SBT]: http://code.google.com/p/simple-build-tool/
[SLF4J]: http://www.slf4j.org/
[Grizzled Scala]: http://software.clapper.org/grizzled-scala/

Version 0.3.1:

* Added more unit tests.
* Upgraded [Grizzled Scala][] dependency to version 1.0.3.
* Now compiles against [Scala][] 2.8.1, as well as 2.8.0.

[Grizzled Scala]: http://bmc.github.com/grizzled-scala/
[Scala]: http://www.scala-lang.org/



Version 0.3:

* Now published to the [Scala Tools Maven repository][], which [SBT][]
  includes by default. Thus, if you're using SBT, it's longer necessary to
  specify a custom repository to find this artifact.

[Scala Tools Maven repository]: http://www.scala-tools.org/repo-releases/
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
