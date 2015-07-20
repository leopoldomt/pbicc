#!/bin/bash

## directories
PROJECT_HOME=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
SRC=$PROJECT_HOME/src
ICC=$SRC/icc
LIBS=$PROJECT_HOME/libs
OUT=$PROJECT_HOME/out

if [ -z "$1" ]
    then
        echo "You must pass the subject's path as input!"
        exit 1
fi
SUBJECT_DIR=$1
SUBJECT_NAME=$(basename $SUBJECT_DIR)
## clean-compile code 
mkdir -p $OUT
rm -rf $OUT/*
for x in `ls $LIBS/*.jar`
do
    CP=$CP:$x
done
javac -cp $CP $ICC/*.java
## this script processes icc for a single app
echo "*** subject $SUBJECT_NAME ***"
JAVA_FILES=$OUT/$SUBJECT_NAME-javafiles.txt
## our current analysis processes java source
(cd $SUBJECT_DIR; find . -name "*.java" ) > $JAVA_FILES

java -cp $CP:$OUT:$SRC icc.Main $JAVA_FILES $SUBJECT_DIR > $OUT/$SUBJECT_NAME-graph-summary.txt
