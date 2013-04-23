#!/use/bin/python

import sys
import random

pairsFile = sys.argv[1]
items = []

f = open(pairsFile, "r")
for line in f.readlines():
	splitLine = line.rstrip().split(" ")
	r = random.randrange(1, 25, 1)
	if r < 1:
		r = r + 1
		
	splitLine.append(str(r))
	items.append(splitLine)
	print splitLine

f.close()

f = open(pairsFile + ".out", "w")
for line in items:
	f.write(line[0] + " " + line[1] + " " + line[2] + "\n")
	
f.close()
