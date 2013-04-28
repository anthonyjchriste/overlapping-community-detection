#!/bin/bash

DATASET="$1"
DATADIR="$2"
VERSION=1.2
UTIL_BASE="OCDUtils-$VERSION.jar $DATASET $DATADIR"
OCD_BASE="OverlappingCommunityDetection-$VERSION.jar $DATASET $DATADIR"

if [ $# -lt 2 ]
then
	echo "Usage: sh batch-run.sh 'data set' 'data dir'"
	exit 1
fi

java -jar $UTIL_BASE convert g2p
java -jar $OCD_BASE -s -w

for i in {1..9}
do
	java -jar $OCD_BASE -w -c -t 0.$i
	java -jar $UTIL_BASE sort
	java -jar $UTIL_BASE dups
	java -jar $UTIL_BASE convert c2g
	mv $DATADIR/$DATASET.clusters $DATADIR/$DATASET.$i.clusters
	mv $DATADIR/$DATASET.clusters.sorted $DATADIR/$DATASET.$i.clusters.sorted
	mv $DATADIR/$DATASET.clusters.sorted.nodups $DATADIR/$DATASET.$i.clusters.sorted.nodups
	mv $DATADIR/$DATASET.clusters.sorted.nodups.named.graphml $DATADIR/$DATASET.$i.clusters.graphml
	mv $DATADIR/$DATASET.cluster_stats $DATADIR/$DATASET.$i.cluster_stats
done
