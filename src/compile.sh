HOME_DIR=/Users/vishaalgopalan/Desktop/Softwares/apache-tomcat-8.5.9/lib

export CLASSPATH=$HOME_DIR/gson-2.6.1.jar:$HOME_DIR/servlet-api.jar

JDK_HOME=/usr/bin

$JDK_HOME/javac -d jar addToDB.java
$JDK_HOME/javac -d jar updateWebsites.java

cd jar
$JDK_HOME/jar -cvf com.jar com
cp com.jar ../../WEB-INF/lib
cp -rf com.jar ../../../../lib
cd ..

