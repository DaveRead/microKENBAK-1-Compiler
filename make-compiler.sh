#!/bin/bash

# Make the compiler for generating microKENBAK-1 machine code
# The Java Development Kit (JDK) must be installed

# Function for error reporting - exits script if error occurs
function error() {
 echo 2>&1
 echo "*******************************************************" 2>&1
 echo "* THE SCRIPT FAILED - SEE ERROR MESSAGE BELOW"  2>&1
 echo "*" 2>&1
 echo "* $1" 2>&1
 echo "*******************************************************" 2>&1
 exit 1
}

LIB_CLASSPATH=
for i in `ls ./lib/*.jar`
do
  LIB_CLASSPATH=${THE_CLASSPATH}:${i}
done

mkdir -p bin

javac -cp $LIB_CLASSPATH -d ./bin -sourcepath ./src/java ./src/java/us/daveread/microkenbak1/compiler/Compiler.java

