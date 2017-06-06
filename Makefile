JDK        = /usr/lib/jvm/java-8-oracle
PWD=`pwd`
CLASSPATH=`pwd`
LD_LIBRARY_PATH=`pwd`


all:    server client java.policy # make all
kill: 
	@pkill  rmiregistry 2>/dev/null 1>/dev/null|| true
	@pkill  java 2>/dev/null 1>/dev/null|| true
	@sleep 0.5
	@pkill  rmiregistry 2>/dev/null 1>/dev/null|| true
	@pkill  java 2>/dev/null 1>/dev/null|| true

run: clean all
	@(CURR_PATH=$(PWD); cd /tmp ; CLASSPATH=$$CURR_PATH $(JDK)/bin/rmiregistry 2099 -Djava.rmi.server.codebase=file://$$CURR_PATH/ ) &
	@( LD_LIBRARY_PATH=`pwd` $(JDK)/bin/java -cp . -Djava.security.policy=$(PWD)/java.policy -Djava.rmi.server.codebase=file://$(PWD)/ Server s0 2099) &
	@( LD_LIBRARY_PATH=`pwd` $(JDK)/bin/java -cp . -Djava.security.policy=$(PWD)/java.policy -Djava.rmi.server.codebase=file://$(PWD)/ Server s1 2099) &
	@( LD_LIBRARY_PATH=`pwd` $(JDK)/bin/java -cp . -Djava.security.policy=$(PWD)/java.policy -Djava.rmi.server.codebase=file://$(PWD)/ Server s2 2099) &
	@sleep 1
	@$(JDK)/bin/java -Djava.security.policy=$(PWD)/java.policy Client localhost localhost localhost 2099

server: Server.java ServerInterface.java \
	GarageData.java Stall.java # server files
	@$(JDK)/bin/javac ServerInterface.java \
	Server.java GarageData.java Stall.java
	@rm -f *.o
	
client: Client.java ClientData.java# client files
	@$(JDK)/bin/javac Client.java ClientData.java

clean: kill # remove class files
	@rm -f Client *.class *.so *.h *.o

