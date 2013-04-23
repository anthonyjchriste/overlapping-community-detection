#!/usr/bin/python

import sys

c_file = sys.argv[1]
p_file = sys.argv[2]

def swap(a, b):
	if int(a) > int(b):
		return b, a
	else:
		return a, b

def create_list(fn):
	f = open(fn, "r")
	res_list = []
	s = set()
	for line in f.readlines():
		l = []
		pairs = line.rstrip().split(" ")
		for pair in pairs:
			vals = pair.split(",")
			l.append(swap(vals[0], vals[1]))
		res_list.append(l)
		
		# Find those extra single pair communities
		if len(pairs) == 1:
			vals = pairs[0].split(",")
			swapped = tuple(swap(vals[0], vals[1]))
			if not swapped in s:
				s.add(swapped)
			else:
				# If we only want to reorganize a single file, don't print out dups
				if not c_file == p_file:
					print "DUP " + fn + " " + str(swapped)
	f.close()
	return res_list, s
	
def write_file(fn, l):
	f = open(fn + ".sorted", "w")
	for comm in l:
		for pair in comm:
			f.write(pair[0] + "," + pair[1] + " ")
		f.write("\n");
	f.close()

c_list, c_set = create_list(c_file)
p_list, p_set = create_list(p_file)

c_list.sort(key = len, reverse=True)
p_list.sort(key = len, reverse=True)

for l in c_list:
	l.sort()
	
for l in p_list:
	l.sort()

write_file(c_file, c_list)
write_file(p_file, p_list)

for pair in c_set:
	if not pair in p_set:
		print str(pair) + "c not p"

for pair in p_set:
	if not pair in c_set:
		print str(pair) + "p not c"
