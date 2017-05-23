CWD=$(shell pwd)
JAVA=java
JAVAC=javac -encoding iso-8859-1
BIN=$(CWD)/bin
SRC=$(CWD)/src
LIB=$(CWD)/lib
# Cliente
CLI=$(SRC)/medicaclient
# Servidor
SRV=$(SRC)/medicaws
SRVLIBDIR=medicaws
SRVLIB=$(LIB)/$(SRVLIBDIR)
SRVJAR=$(LIB)/medicaws.jar
SRVCP=$(LIB)/jackson-databind-2.8.5.jar:$(LIB)/jackson-core-2.8.5.jar:$(LIB)/jackson-annotations-2.8.5.jar
# Configuracion de Java
AXIS=$(CWD)/axis-bin-1_4.tar.gz
AXISHOME=$(CWD)/axis-1_4
AXISLIB=$(AXISHOME)/lib
AXISCP=$(AXISLIB)/axis-ant.jar:$(AXISLIB)/commons-logging-1.0.4.jar:$(AXISLIB)/axis.jar:$(AXISLIB)/jaxrpc.jar:$(AXISLIB)/saaj.jar:$(AXISLIB)/commons-discovery-0.2.jar:$(AXISLIB)/log4j-1.2.8.jar:$(AXISLIB)/wsdl4j-1.5.1.jar:$(LIB)/mail.jar:$(LIB)/activation.jar:$(SRVCP):$(SRVJAR)
AXISWEB=$(AXISHOME)/webapps
AXISPORT=8888

all: client jar

jdk-version-check:
	@echo -e "javac 1.8\n$(shell javac -version 2>&1)" | sort -ct. -k1,1n -k2,2n -k3,3n 2>/dev/null || ( echo "Tu version de Java JDK es menor a 1.8.0" && exit 1 )

axis: configure jar
	cd $(AXISWEB) && $(JAVA) -cp "$(AXISCP)" org.apache.axis.transport.http.SimpleAxisServer -p $(AXISPORT)

deploy:
	cd $(AXISWEB) && cp $(CWD)/deploy.wsdd axis/ && $(JAVA) -cp "$(AXISCP)" org.apache.axis.client.AdminClient -p $(AXISPORT) axis/deploy.wsdd

configure:
	tar xzf $(AXIS) -C $(CWD)

server:
	$(JAVAC) -cp "$(SRVCP)" -d $(LIB) $(SRV)/*.java

jar: server
	cd $(LIB) && jar cvf $(SRVJAR) $(SRVLIBDIR)/*.class

client: jdk-version-check configure jar
	mkdir -p $(BIN) && $(JAVAC) -cp "$(AXISCP)" -d $(BIN) $(CLI)/*.java

clean:
	rm -rf $(AXISHOME) $(SRVJAR) $(BIN)/*/*.class $(LIB)/$(SRVLIB)
