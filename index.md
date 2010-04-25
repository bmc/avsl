---
title: AVSL, A Very Simple Logger
layout: withTOC
---

## NOTICE

**THIS PACKAGE IS STILL UNDER DEVELOPMENT! THIS DOCUMENTATION IS NOT
FINISHED! YOUR FLY MAY BE OPEN!**

**AVSL is not fully implemented yet. Don't expect it to work completely,
and don't expect it not to make off with your first-born child.**

**This notice will self-destruct when I decide AVSL is cooked all the way
to the center and the juices are no longer red.**

## Introduction

AVSL is a very simple logging framework, written in [Scala][]. AVSL
implements the [SLF4J][] API, allowing applications to be written to the
[SLF4J][] API. (This, of course, includes Java applications.) Because it
implements SLF4J, AVSL can easily be swapped for another SLF4J-compatible
logging framework (or the other way around), without any changes to the
calling application. Also, because it supports SLF4J, AVSL can be used in
conjunction with Scala SLF4J wrappers, such as [Grizzled-SLF4J][].

"AVSL" stands for "A Very Simple Logger", and AVSL strives for simplicity
in several ways.

* AVSL is simple to configure, using a non-XML, INI-style configuration
  file that's reminiscent of the [Python logging module]'s configuration.
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

In short, AVSL is a perfectly serviceable, simple logging framework that can
easily be swapped out for something more powerful.

## More coming

There's more coming...

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

[BSD License]: license.html
[GitHub repository]: http://github.com/bmc/avsl
[Grizzled-SLF4J]: http://bmc.github.com/grizzled-slf4j/
[GitHub]: http://github.com/bmc/
[downloads area]: http://github.com/bmc/avsl/downloads
[*clapper.org* Maven repository]: http://maven.clapper.org/org/clapper/
[Maven]: http://maven.apache.org/
[bmc@clapper.org]: mailto:bmc@clapper.org
[Scala]: http://www.scala-lang.org/
[Python logging module]: http://docs.python.org/library/logging.html
[SLF4J]: http://slf4j.org/
[Logback]: http://logback.qos.ch/
