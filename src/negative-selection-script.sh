#!/bin/bash

# This simple script illustrates how one would use the automata generation codes 

N=6
k=2

mkdir -p tmp

# Step 1 : Make an automaton that contains positively selected repertoire.
# (It can make sense to put this first step into a different script or put 
# it into the makefile, because this can take long.)

cat self.txt | ./pattern-exact-fa $N $k | fstcompile --acceptor > tmp/self-$k.fst

# Step 2 : Make an automaton that contains all possible TCR.

./makerep-pattern-exact-fa $N $k | fstcompile --acceptor > tmp/all-$k.fst

# Step 3: Subtract positively selected from all TCR to obtain negatively selected TCR.

fstdifference tmp/all-$k.fst tmp/self-$k.fst | fstminimize > tmp/neg-$k.fst

# Step 4: Compute precursor frequencies for all foreign epitopes.

for i in $(seq 1 $(wc -l < hiv.txt)) ; do
	p=$(awk "NR==$i" hiv.txt)
	echo -n $p" "
	echo $p | ./pattern-exact-fa $N $k | fstcompile --acceptor | fstintersect tmp/neg-$k.fst - | fstprint | ./countpaths 
done
