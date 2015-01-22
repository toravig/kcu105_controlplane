#!/bin/sh
DMA_MODULE_NAME="xdma_ctrl"
STATSFILE="xdma_ctrl"

if [ -d /sys/module/$DMA_MODULE_NAME ]; then
                echo "first if"
		sudo rmmod $DMA_MODULE_NAME
fi
if [ -c /dev/$STATSFILE ]; then
        echo "second if"
	sudo rm -rf /dev/$STATSFILE
fi

