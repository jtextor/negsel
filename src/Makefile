all : contiguous-fa countpaths mjc-fa

%-fa : %-fa.cpp dfa.hpp idfa.hpp refinable-partition.hpp aminoacids.hpp miyazawa-jernigan-matrix.hpp
	g++ -O3 -std=c++0x -o $*-fa $*-fa.cpp

printlang: printlang.cpp
	g++ -O3 -o $@ $<

countpaths: countpaths.cpp
	g++ -O3 -o $@ $<
