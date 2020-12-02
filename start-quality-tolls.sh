#!/bin/bash
echo '**************************************************************'
echo '*                                                            *'
echo '*                Quality Tools Rian                          *'
echo '*                                                            *'
echo '**************************************************************' 

mvn verify -P cobertura
#mvn checkstyle:checkstyle
mvn verify -P owasp -DskipTests=false

mvn jxr:jxr pmd:pmd -DskipTests=false
#mvn pmd:check

mvn spotbugs:spotbugs -DskipTests=false
mvn spotbugs:spotbugs -P security -DskipTests=false
mvn verify sonar:sonar -DskipTests=false