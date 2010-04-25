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
easily be swapped out for something more powerful.

## Using the API

The simplest (and recommended) way to use AVSL is via SLF4J. That way, if
you want to switch to another logging framework, you don't have to change
your code.

### Using SLF4J directly

If you're using SLF4J directly, you can simply instantiate an SLF4J logger.
Here's a Java example:

    import org.slf4j.*;

    class MyClass
    {
        private Logger logger = LoggerFactory.getLogger(MyClass.class)

        ...

        public void someMethod()
        {
            logger.debug("Entering someMethod()")
            ...
            logger.debug(Exiting someMethod()")
        }
    }

Here's a Scala example:

    import org.slf4j._
    
    class MyClass
    {
        val logger = LoggerFactory.getLogger(classOf[MyClass])

        def someMethod =
        {
            logger.debug("Entering someMethod()")
            ...
            logger.debug(Exiting someMethod()")
        }
    }

### Using SLF4J via the Grizzled-SLF4J wrapper

If you're writing in Scala, you may want to use a more Scala-friendly wrapper.
One example is my [Grizzled-SLF4J][] wrapper. Here's the above example,
with Grizzled-SLF4J:

    import grizzled.slf4j._
    
    class MyClass
    {
        val logger = Logger(classOf[MyClass])

        def someMethod =
        {
            logger.debug("Entering someMethod()")
            ...
            logger.debug(Exiting someMethod()")
        }
    }

## Log levels

Like most logging frameworks, AVSL segregates log messages into logging
levels, allowing fine-grained control over the levels. It supports the
following levels:

- `Error` (value 50)
- `Warn` (value 40)
- `Info` (value 30)
- `Debug` (value 20)
- `Trace` (value 10)

Log messages are tagged with log levels and are only displayed if:

1. The logger's log level is equal to or numerically lower than the message's
   level. e.g., If the logger is configured at level `Info`, then messages
   written at level `Debug` will be suppressed.
2. The handlers associated with the logger have levels that are equal or
   numerically lower than the message's level. This approach allows you to
   direct messages to different handlers, depending on their log levels.

## Hierarchical loggers

Like most logging frameworks, AVSL's loggers are hierarchical. At the top
of the hierarchy sits the root logger. Underneath the root logger are
hierarchies of named loggers. If the calling program requests the logger
for a specific name, AVSL uses the most specific logger it can find for 
that name, defaulting to the top-level root logger if nothing more specific
can be found. By convention, logger names are class names, which fit neatly
into a hierarchy.

An example will help clarify the approach. Consider a configuration that
specifies the following individual (named) loggers:

    root (log level ERROR)
    org.example.myapp (log level DEBUG)
    org.example.myapp.io (log level ERROR)
    org.clapper.grizzled (log level ERROR)
    com.example.superapi (log level INFO)
    com.example.superapi.math (log level DEBUG)

Internally, AVSL will convert such a configuration to the following tree
of loggers:

    root (ERROR)
     |
     +-- [org]
     |    |
     |    +-- [example]
     |    |     |
     |    |     +-- myapp (DEBUG)
     |    |           |
     |    |           +-- io (ERROR)
     |    |
     |    +-- grizzled (ERROR)
     |
     +-- [com]
          |
          +-- [example]
                |
                +-- superapi (INFO)
                       |
                       +-- math (DEBUG)

In the diagram, names in brackets are placeholders; they exist in the tree,
to establish the hierarchy, but they have no associated loggers.

If a calling program using the above configuration asks for a named logger,
AVSL will find the most specific logger for the name. Here are some examples:

- `org.example.myapp`: AVSL will return the configured logger for this name,
   with level DEBUG.
- `org.example`: Neither `org.example` nor `org` has a configured logger, so
  AVSL returns the root logger, with level ERROR.
- `grizzled.io`: There's no configured logger for `grizzled.io`, but there *is*
  a configured logger for the parent `grizzled` name, so AVSL returns that
  logger, with level ERROR.
- `com.example.superapi.math`: AVSL returns the configured logger for this
  name, with level DEBUG.
- `com.example.superapi`: AVSL returns the configured logger for this
  name, with level INFO.
- `com.example.superapi.util`: There is no configured logger for
  `com.example.superapi.util`, so AVSL returns the logger for
  `com.example.superapi`, with level INFO.
- `org.scala-tools`: Neither `org.scala-tools` nor `org` has a configured
  logger, so AVSL returns the root logger.

## Configuring AVSL

AVSL uses a simple INI-style configuration file, reminiscent of the
[Python logging module][]'s configuration. This section describes that
file in detail.

### Configuration file syntax

The configuration file consists of three kinds of sections:

- Logger sections (whose names start with `logger_`) configure specific
  loggers.
- Handler sections (whose names start with `handler_`) configure message
  handlers, which are responsible to dispatching log messages to various
  places (files, the screen, etc.)
- Formatter sections (whose names start with `formatter_`) configure
  message formatters, which control the formats of log messages.

### Specifying the location of the configuration file at runtime

## Running with AVSL

To run your program with AVSL, you'll need to have the following jar files
in your CLASSPATH at runtime:

- The AVSL jar file
- The `slf4j-api.jar` jar file (assuming you're using the SLF4J interface,
  which is recommended).
- The `grizzled-slf4j.jar` jar file, if you're using the [Grizzled-SLF4J][]
  Scala-SLF4J wrapper.

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
