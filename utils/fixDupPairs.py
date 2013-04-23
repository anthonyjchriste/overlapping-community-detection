#!/usr/bin/python

import sys

fname = sys.argv[1]
f = open(fname, "r")

pairs = []

dspldups = True

for line in f:
	split = line.rstrip("\n").split(" ")
	if not (split[0], split[1]) in pairs:
		if not (split[1], split[0]) in pairs:
			if len(split) == 3: # Weighted
				pairs.append((split[0], split[1], split[2]))
			else:
				pairs.append((split[0], split[1]))
		else:
			if dspldups:
				print "DUP " + split[1] + " " + split[0]
	else:
		if dspldups:
			print "DUP " + split[0] + " " + split[1]
	
f.close()

if not dspldups:
	for pair in pairs:
		if len(pair) == 3:
			print pair[0] + " " + pair[1] + " " + pair[2]
		else:
			print pair[0] + " " + pair[1]
