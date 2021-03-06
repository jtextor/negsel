# Negative Selection
String-based models of thymic selection in the immune system.

This is an implementation of the techniques described in:

Johannes Textor, Katharina Dannenberg, Maciej Liskiewicz:
__A Generic Finite Automata Based Approach to Implementing Lymphocyte Repertoire Models.__
In _Proceedings of the 2014 conference on genetic and evolutionary computation (GECCO'14)_, pp. 129-137. ACM, 2014. http://doi.org/10.1145/2576768.2598331


There is also an older Java program that implements the algorithms described here:

Michael Elberfeld, Johannes Textor:
__Negative Selection Algorithms on Strings with Efficient Training and Linear-Time Classification.__
Theoretical Computer Science, 412(6):534-542, 2011. https://doi.org/10.1016/j.tcs.2010.09.022

## Dependencies

You will need a C++ compiler and the OpenFST library binaries installed (http://www.openfst.org). On Mac OS X, you can install these with homebrew using

```brew install openfst```

## Usage

The code generates deterministic automata (DFAs) that recognize certain sets of strings of fixed length. The alphabet is defined in "proteins.hpp" and it is normally taken to be the 20-letter amino acid alphabet (of course, this can be adapted).

For example, the file "contiguous-fa.cpp" implements the so-called r-contiguous matching rule. 
