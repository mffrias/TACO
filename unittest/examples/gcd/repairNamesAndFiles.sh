#\!/bin/bash

for VAR in {40..60} 
do
        eval "sed -i '' 's/\.ror\.ror40\./\.ror\.ror$VAR\./' ROR$VAR/StrykerGcdTestROR$VAR.java"
        eval "sed -i '' 's/ROR40/ROR$VAR/' ROR$VAR/StrykerGcdTestROR$VAR.java"
done
