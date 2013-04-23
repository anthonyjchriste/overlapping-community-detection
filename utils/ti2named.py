#!/usr/bin/python

import sys

stats = sys.argv[1]
clusters = sys.argv[2]

hashed = {}

stats_f = open(stats, "r")
for line in stats_f.readlines():
	split_line = line.rstrip().split(":")
	hashed[split_line[1]] = split_line[0]
	
stats_f.close()

lines = []

clusters_f = open(clusters, "r")
for line in clusters_f.readlines():
	com = ""
	split_line = line.rstrip().split(" ")
	for pair in split_line:
		split_pair = pair.rstrip().split(",")
		if split_pair[0] in hashed:
			com = com + hashed[split_pair[0]] + "," + split_pair[1] + ":"
		else:
			com = com + split_pair[0] + "," + hashed[split_pair[1]] + ":"
		
	lines.append(com[:-1])
clusters_f.close()		

out_f = open(clusters + ".named", "w")
for line in lines:
	out_f.write(line.rstrip() + "\n")
	
out_f.close()
