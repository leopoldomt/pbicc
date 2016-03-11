#!/bin/bash

## consider writing this in python (not priority)

## directories
PROJECT_HOME=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
SRC=$PROJECT_HOME/src
ICC=$SRC/icc
OUT=$PROJECT_HOME/out

if [ -z "$1" ]; then
    echo "You must pass the subject's path as input!  For example $> ./run.sh test-data/zooborns/"
    exit 1
fi
if [ ! -f "target/pb-icc.jar" ]; then
    echo "Main jar file not found. Run \"mvn clean install\" and try again"
    exit 1
fi

SUBJECT_DIR=$1
SUBJECT_SRC=$SUBJECT_DIR/src/
SUBJECT_NAME=$(basename $SUBJECT_DIR)
## clean-compile code 
mkdir -p $OUT
rm -rf $OUT/*

## this script processes icc for a single app
echo "*** subject $SUBJECT_NAME ***"
JAVA_FILES=$OUT/$SUBJECT_NAME-javafiles.txt

## our current analysis processes java source
(cd $SUBJECT_SRC; find . -name "*.java" ) > $JAVA_FILES

## execute analysis and check its status code.
java -jar target/pb-icc.jar $JAVA_FILES $SUBJECT_SRC $(find $SUBJECT_DIR -name AndroidManifest.xml)
if [ "$?" -ne 0 ]; then
    exit 1
fi

echo "check directory out for generated files.  For example, file zooborns-graph-summary.txt summarizes the contents of the ICC graph generated"

##echo "assuming you have graphviz installed"
##(cd $OUT;
##    dot -Tjpg ${SUBJECT_NAME}-cdg.dot > ${SUBJECT_NAME}-cdg.jpg
##    open ${SUBJECT_NAME}-cdg.jpg
##)
