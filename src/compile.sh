#HOME_DIR=path of the tomcat/lib directory

export CLASSPATH=$HOME_DIR/gson-2.6.1.jar:$HOME_DIR/servlet-api.jar

#JDK_HOME= jdk path

$JDK_HOME/javac -d jar addToDB.java
$JDK_HOME/javac -d jar updateWebsites.java

#create a directory name "jar" with a sub-directory "syndicate" in "src" folder. this is where the class files will reside
#after they are compiled
cd jar
$JDK_HOME/jar -cvf com.jar com
#copies the jar file to the lib directory in the WEB-INF folder
cp com.jar ../../WEB-INF/lib
#copies the jar file to the tomcat/lib directory
cp -rf com.jar ../../../../lib
cd ..
