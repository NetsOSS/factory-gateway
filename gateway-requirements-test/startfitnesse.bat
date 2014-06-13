@echo off

title Payment Fitnesse Server
echo Starter Payment Fitnesse Server...
call mvn clean install
for /f %%a in (target\standalone.classpath)	do set standalone.classpath=%%a
java -cp target/test-classes;target/classes;%standalone.classpath% eu.nets.payment.fitnesse.example.ExampleFitnesseServer

