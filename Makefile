CWD=$(shell pwd)
JAVA=java
JAVAC=javac -encoding iso-8859-1
BIN=$(CWD)/bin
SRC=$(CWD)/src
LIB=$(CWD)/lib
RES=$(CWD)/res
# Cliente
CLISRCDIR=medicaclient
CLI=$(SRC)/$(CLISRCDIR)
CLIBIN=$(BIN)/$(CLISRCDIR)
# Servidor
SRV=$(SRC)/medicaws
SRVSRCDIR=medicaws
SRVLIB=$(LIB)/$(SRVSRCDIR)
SRVJAR=$(LIB)/medicaws.jar
SRVCP=$(LIB)/jackson-databind-2.8.5.jar:$(LIB)/jackson-core-2.8.5.jar:$(LIB)/jackson-annotations-2.8.5.jar
# Configuracion de Java
AXIS=$(RES)/axis-bin-1_4.tar.gz
AXISHOME=$(CWD)/axis-1_4
AXISLIB=$(AXISHOME)/lib
AXISCP=$(AXISLIB)/axis-ant.jar:$(AXISLIB)/commons-logging-1.0.4.jar:$(AXISLIB)/axis.jar:$(AXISLIB)/jaxrpc.jar:$(AXISLIB)/saaj.jar:$(AXISLIB)/commons-discovery-0.2.jar:$(AXISLIB)/log4j-1.2.8.jar:$(AXISLIB)/wsdl4j-1.5.1.jar:$(LIB)/mail.jar:$(LIB)/activation.jar:$(SRVCP):$(SRVJAR)
AXISWEB=$(AXISHOME)/webapps
AXISPORT=8888

all: configure jar buildclient

jdk-version-check:
	@echo -e "javac 1.8\n$(shell javac -version 2>&1)" | sort -ct. -k1,1n -k2,2n -k3,3n 2>/dev/null || ( echo "Tu version de Java JDK es menor a 1.8.0" && exit 1 )

configure:
	tar xzf $(AXIS) -C $(CWD)

buildserver:
	$(JAVAC) -cp "$(SRVCP)" -d $(LIB) $(SRV)/*.java

jar: buildserver
	cd $(LIB) && jar cvf $(SRVJAR) $(SRVSRCDIR)/*.class

buildclient: jdk-version-check configure jar
	mkdir -p $(BIN) && $(JAVAC) -cp "$(AXISCP)" -d $(BIN) $(CLI)/*.java

clean:
	rm -rf $(AXISHOME) $(SRVJAR) $(SRVLIB) $(CLIBIN)

samples:
	cp $(RES)/citas.json.example $(AXISWEB)/citas.json && cp $(RES)/cuentas.json.example $(AXISWEB)/cuentas.json

axis: samples
	cd $(AXISWEB) && $(JAVA) -cp "$(AXISCP)" org.apache.axis.transport.http.SimpleAxisServer -p $(AXISPORT)

deploy:
	cd $(AXISWEB) && cp $(RES)/deploy.wsdd axis/ && $(JAVA) -cp "$(AXISCP)" org.apache.axis.client.AdminClient -p $(AXISPORT) axis/deploy.wsdd

client:
	@$(JAVA) -cp "$(AXISCP):$(BIN)" $(CLISRCDIR)/MedicaClient

