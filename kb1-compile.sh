#!/bin/bash

# Run the compiler for generating microKENBAK-1 machine code

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

# Verify the command line has one or two arguments
if [[ "$#" -eq 0 || "$#" -gt 2 ]]; then
 error "You must supply the name of the source code file. Optionally a second argument may be provided (output file name or --ASHTML)"
fi

LIB_CLASSPATH=
for i in `ls ./lib/*.jar`
do
  LIB_CLASSPATH=${THE_CLASSPATH}:${i}
done

java -cp bin:$LIB_CLASSPATH us.daveread.microkenbak1.compiler.Compiler $@

