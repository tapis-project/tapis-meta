# Don't use GUI mode for load testing !, only for Test creation and Test debugging.
# For load testing, use CLI Mode (was NON GUI):
#    jmeter -n -t [jmx file] -l [results file] -e -o [Path to web report folder]
#  & increase Java Heap to meet your test requirements:
#   Modify current env variable HEAP="-Xms1g -Xmx1g -XX:MaxMetaspaceSize=256m" in the jmeter batch file
# Check : https://jmeter.apache.org/usermanual/best-practices.html

export HEAP="-Xms2g -Xmx2g -XX:MaxMetaspaceSize=256m"
jmeter -n -t meta-Group.jmx -l ./test-results/results/results.csv -e -o ./test-results/results

