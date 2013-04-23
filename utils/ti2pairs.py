#!/usr/bin/python

import sys

lines = []
resultLines = []
hashed = {}

cur_id = 100000 # max node is 93877

# Read in extra info pairs file
f = open(sys.argv[1], "r")

for line in f.readlines():
        splitLine = line.rstrip().split("\t")
        lines.append([splitLine[0], splitLine[1], splitLine[5]])
        
f.close()

# Strip out all words and replace with unique id        
for line in lines:
        a = line[0]
        b = line[1]
        w = line[2]
        
        if a.isdigit():
                na = int(a)
                if not b in hashed:
                        hashed[b] = cur_id
                        nb = cur_id
                        cur_id = cur_id + 1
                else:
                        nb = hashed[b]
        else:
                nb = int(b)
                if not a in hashed:
                        hashed[a] = cur_id
                        na = cur_id
                        cur_id = cur_id + 1
                else:
                        na = hashed[a]

        #print str(na) + " " + str(nb) + " " + str(w)
        resultLines.append(str(na) + " " + str(nb) + " " + str(w))

# Write out pairs file
f = open(sys.argv[1] + ".pairs", "w")
for line in resultLines:
	f.write(line + "\n")

f.close()

# Write out hash key
f = open(sys.argv[1] + ".stats", "w")
for item in hashed:
	f.write(item + ":" + str(hashed[item]) + "\n")
f.close()




