#!/usr/bin/env python3

import sys

if len(sys.argv) < 2:
	sys.exit("Skripta prima jedan argument - ime datoteke koju treba urediti") 

file = sys.argv[1]

fhead = open(file + '.head', 'r')
fdat = open(file + '.dat', 'r')
fmod = open(file + '.mod', 'w')
ftxt = open(file + '.txt', 'w')

bounds = []
for line in fhead.readlines():
	(l, r) = line.rstrip().split(" ")[-2:]
	bounds.append((float(l[1:-1]), float(r[:-1])))

for line in fdat.readlines():
	parts = line.rstrip().split(',')
	
	vector = []
	for i in range(len(parts)-1):
		vector.append(str((float(parts[i]) - bounds[i][0]) / (bounds[i][1] - bounds[i][0]))) 
	
	ftxt.write(" ".join(vector) + "\n")
	vector.append(str(int(parts[-1])))
	fmod.write(",".join(vector) + "\n")
