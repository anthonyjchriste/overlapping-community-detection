#!/bin/bash

DATASET="$1"
DATADIR="$2"
VERSION=1.2
UTIL_BASE="OCDUtils-$VERSION.jar $DATASET $DATADIR"
OCD_BASE="OverlappingCommunityDetection-$VERSION.jar $DATASET $DATADIR"
TIME="/usr/bin/time -v"

# Add $TIME to below command for performance measurements
JAR_CMD="java -jar"

if [ $# -lt 2 ]
then
    echo "Usage: bash batch-run.sh 'data set' 'data dir'"
    exit 1
fi

echo "Converting GraphML to .pairs"
$JAR_CMD $UTIL_BASE convertw g2p

echo "Calculating similarities"
$JAR_CMD $OCD_BASE -s -w

for i in {1..9}
do
    echo "Converting GraphML to .pairs"
    $JAR_CMD $OCD_BASE -w -c -t 0.$i
    
    echo "Sorting communities"
    $JAR_CMD $UTIL_BASE sort
    
    echo "Removing duplicates"
    $JAR_CMD $UTIL_BASE dups
    
    echo "Converting .ckusters to GraphML"
    $JAR_CMD $UTIL_BASE convertw c2g
    
    echo "Cleaning up data"
    mv $DATADIR/$DATASET.clusters $DATADIR/$DATASET.$i.clusters
    mv $DATADIR/$DATASET.clusters.sorted $DATADIR/$DATASET.$i.clusters.sorted
    mv $DATADIR/$DATASET.clusters.sorted.nodups $DATADIR/$DATASET.$i.clusters.sorted.nodups
    mv $DATADIR/$DATASET.clusters.graphml $DATADIR/$DATASET.$i.clusters.graphml
    mv $DATADIR/$DATASET.cluster_stats $DATADIR/$DATASET.$i.cluster_stats
    mkdir -p $DATADIR/GraphML
    mv $DATADIR/*.clusters.graphml $DATADIR/GraphML/.
done
