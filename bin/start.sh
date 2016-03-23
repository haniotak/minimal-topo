#!/bin/bash
JAVA_OPTS=" $JAVA_OPTS -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true "

java $JAVA_OPTS -jar target/minimal-topo-1.0-SNAPSHOT.jar $*
