#!/bin/bash
# Script that runs icc/AllTests.java

javac -cp libs/*:src src/icc/*.java
javac -cp libs/*:src:tests tests/icc/*.java
java -cp test-data:libs/*:src:tests org.junit.runner.JUnitCore icc.AllTests

