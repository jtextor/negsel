#!/bin/bash

for d in org algorithms alphabets util ; do
   echo "compiling $d ..."
   find $d -name '*.java' | xargs javac 
done

echo "compiling Main.java ..."
javac Main.java

echo "compiling SeqLogo.java ..."
javac SeqLogo.java

jar cfe ../negsel2.jar Main .

