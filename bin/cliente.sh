#!/bin/bash
JAVA=java
if [ -f MedicaClient.class ]; then
    $JAVA MedicaClient "$@"
else
    echo "Necesitas compilar el cliente: \$ make client"
    exit 1
fi
