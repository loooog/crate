.. highlight:: sh
.. _cli:

===============
Running CrateDB
===============

This document covers the basics of running CrateDB from the command line.

.. NOTE::

   For help installing CrateDB, check out `Getting Started With CrateDB`_.

   For help deploying CrateDB, check out the `CrateDB Guide`_.

.. _Getting Started With CrateDB: https://crate.io/docs/crate/getting-started/en/latest/install/index.html
.. _CrateDB Guide: https://crate.io/docs/crate/guide/en/latest/deployment/index.html

.. rubric:: Table of Contents

.. contents::
   :local:

Introduction
============

CrateDB ships with a ``crate`` command in the ``bin`` directory.

The simplest way to start a CrateDB instance is to invoke ``crate`` without
parameters. This will start the process in the foreground.

::

  sh$ ./bin/crate

You can also start CrateDB in the background using the ``-d`` option. When
starting CrateDB in the background it is helpful to write the process ID into a
pid file so you can find out the process id easlily::

  sh$ ./bin/crate -d -p ./crate.pid

To stop the process that is running in the background send the ``TERM`` or
``INT`` signal to it.

::

  sh$ kill -TERM `cat ./crate.pid`

The ``crate`` executable supports the following command line options:

Command Line Options
====================

+------------------+----------------------------------------------------------+
| Option           | Description                                              |
+==================+==========================================================+
| ``-d``           | Start the daemon in the background                       |
+------------------+----------------------------------------------------------+
| ``-h``           | Print usage information                                  |
+------------------+----------------------------------------------------------+
| ``-p <pidfile>`` | Log the pid to a file                                    |
+------------------+----------------------------------------------------------+
| ``-v``           | Print version information                                |
+------------------+----------------------------------------------------------+
| ``-C``           | Set a CrateDB :ref:`configuration <config>` value        |
|                  | (overrides configuration file)                           |
+------------------+----------------------------------------------------------+
| ``-D``           | Set a Java system property value                         |
+------------------+----------------------------------------------------------+
| ``-X``           | Set a nonstandard java option                            |
+------------------+----------------------------------------------------------+

Example::

  sh$ ./bin/crate -d -p ./crate.pid

.. _cli_signals:

Signal Handling
===============

The CrateDB process can handle the following signals.

+-----------+---------------------------------------------+
| Signal    | Description                                 |
+===========+=============================================+
| ``TERM``  | Stops a running CrateDB process             |
|           |                                             |
|           | ``kill -TERM `cat /path/to/pidfile.pid```   |
|           |                                             |
+-----------+---------------------------------------------+
| ``INT``   | Stops a running CrateDB process             |
|           |                                             |
|           | Same behaviour as ``TERM``.                 |
+-----------+---------------------------------------------+
| ``USR2``  | Stops a running CrateDB process gracefully. |
|           | See `Rolling Upgrade`_ for more             |
|           | information                                 |
|           |                                             |
|           | ``kill -USR2 `cat /path/to/pidfile.pid```   |
|           |                                             |
|           | ``USR2`` is not supported on Windows.       |
|           |                                             |
+-----------+---------------------------------------------+

.. _Rolling Upgrade: http://crate.io/docs/crate/guide/best_practices/rolling_upgrade.html
