#!/bin/bash

## look for file dependencies

IN=$1

if [ -z "$1" ]
then
    echo "You must pass the subject's path as input!  For example $> ./filedeps.sh test-data/zooborns/"
    exit 1
fi

## for ZooBorns

DOTFILE="/tmp/out.dot"
JPGFILE="/tmp/out.jpg"

echo "digraph {" > ${DOTFILE}
(
for x in `ls $IN/src/*.java`;
do
    namex=$(echo $x | cut -f1 -d. | rev | awk -F/ '{print $1}' | rev)
    grep -w "${namex}" test-data/zooborns//src/*.java | awk -F":" '{print $1}' | sort | uniq | cut -d. -f1 | rev | awk -F"/" '{print $1}' | rev > /tmp/scratch.txt
    while read cli; 
    do
	echo "   $cli -> $namex" >> ${DOTFILE}
    done < /tmp/scratch.txt
done
) | sort
echo "}" >> ${DOTFILE}

dot -Tjpg ${DOTFILE} > ${JPGFILE}