#!/usr/bin/bash

DATA_NAME=$1
DATA_DIR=$2

#echo $DATA_NAME
#echo $DATA_DIR

for i in {1..9}
do
	/usr/bin/time -v java -XX:+AggressiveHeap -classpath ~/CommunityOverlap/java CommunityOverlap $DATA_NAME $DATA_DIR -w -c -t 0.$i
	python ~/CommunityOverlap/scripts/compareComms.py $DATA_DIR/$DATA_NAME.clusters $DATA_DIR/$DATA_NAME.clusters
	java -XX:+AggressiveHeap -classpath ~/CommunityOverlap/scripts RemoveDups $DATA_DIR/$DATA_NAME.clusters.sorted
	python ~/CommunityOverlap/scripts/ti2named.py ~/CommunityOverlap/miscdata/ti_names.txt $DATA_DIR/$DATA_NAME.clusters.sorted.nodups
	java -XX:+AggressiveHeap -classpath ~/CommunityOverlap/scripts clusters2graphml $DATA_DIR/$DATA_NAME.clusters.sorted.nodups.named $DATA_NAME
	mv $DATA_DIR/$DATA_NAME.clusters $DATA_DIR/$DATA_NAME.$i.clusters
	mv $DATA_DIR/$DATA_NAME.clusters.sorted $DATA_DIR/$DATA_NAME.$i.clusters.sorted
	mv $DATA_DIR/$DATA_NAME.clusters.sorted.nodups $DATA_DIR/$DATA_NAME.$i.clusters.sorted.nodups
	mv $DATA_DIR/$DATA_NAME.clusters.sorted.nodups.named $DATA_DIR/$DATA_NAME.$i.clusters.sorted.nodups.named
	mv $DATA_DIR/$DATA_NAME.clusters.sorted.nodups.named.graphml $DATA_DIR/$DATA_NAME.$i.graphml
	mv $DATA_DIR/$DATA_NAME.cluster_stats $DATA_DIR/$DATA_NAME.$i.cluster_stats
done
