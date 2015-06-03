#!/usr/bin/env python3

fin = open('texture.dat', 'r')
fout = open('texture.txt', 'w')

for line in fin.readlines():
	newline = " ".join(line.rstrip().split(',')[:-1])
	fout.write(newline + "\n")
