call mvn clean package
SET ES_HOME=C:\Dev\elasticsearch-1.0.0.Beta1
call %ES_HOME%\bin\plugin -r auto-tagging
call %ES_HOME%\bin\plugin -i auto-tagging -u file:///%CD%/target/releases/elasticsearch-auto-tagging-0.0.1-SNAPSHOT.zip