#!/bin/bash
JAVA=java
if [ -f medicaclient/MedicaClient.class ]; then
    $JAVA medicaclient/MedicaClient "$@"
else
    echo "Necesitas compilar el cliente: \$ make client"
    exit 1
fi
