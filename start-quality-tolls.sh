#!/bin/bash
echo '**************************************************************'
echo '*                                                            *'
echo '*                Quality Tools Rian                          *'
echo '*                                                            *'
echo '**************************************************************' 

mvn verify -P cobertura
mvn checkstyle:checkstyle
mvn verify -P owasp

mvn jxr:jxr pmd:pmd
mvn pmd:check

mvn spotbugs:spotbugs
mvn spotbugs:spotbugs -P security
mvn verify sonar:sonar