
INTRODUCTION

This project provides a basic JAVA interface to the Ether IO family of boards.

The Ether IO family of digital input/output boards allows for remote 
control of digital I/O ports over Ethernet using a UDP based protocol.

For product details see:
   http://www.temperosystems.com.au/
   http://www.temperosystems.com.au/support/

BUILD AND INSTALL INSTRUCTIONS

A Maven build file is included and can be run as follows:

To build source and documentation JAR files: mvn package
To install to the local Maven repository: mvn install
To install without running integration tests which require that hardware be present: mvn install -DskipITs


This software is free to use as governed by the Apache 2.0 Software License.

The author is not affiliated in any way with Tempero Systems.

Copyright (C) 2019 Gerard L. Muir


