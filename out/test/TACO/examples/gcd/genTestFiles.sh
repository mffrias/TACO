#\!/bin/bash

for VAR in {40..60} 
do
    mkdir "./ror$VAR"
    cp StrykerGcdTestROR40.java "./ror$VAR/StrykerGcdTestROR$VAR.java"
done
