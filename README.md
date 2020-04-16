# HAMR Inspector Stream

A "port" of reactor with API changes to support testing and verification of 
reactive event streams.

This project's structure was inspired by https://github.com/reactor/reactor-scala-extensions,
but all code was hand written for this project.


This project also contains a number of tests to verify compliance with the 
reactive-streams tck (https://github.com/reactive-streams/reactive-streams-jvm/tree/master/tck). These tests are Scala ports of Java code from
https://github.com/reactor/reactor-core/tree/master/reactor-core/src/test/java/reactor/core/publisher/tck.
Each Scala port individually credits its reference implementation in a comment above.