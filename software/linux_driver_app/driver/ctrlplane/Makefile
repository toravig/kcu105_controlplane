obj-m := xpcie.c
obj-m := xdma_ctrl.o
xdma_ctrl-objs :=  xpcie.o

KDIR= /lib/modules/$(shell uname -r)/build
#ROOTDIR = $(PWD)

EXTRA_CFLAGS += -DX86_PC -DNWLDMA -DXENV_LINUX -DUSE_IO_MACROS -DXLITTLE_ENDIAN -DPCI_LINUX -I $(ROOTDIR)/include/ -I $(XDMA_CTRL_PATH)  

all:
	$(MAKE) $(CFLAGS) -C $(KDIR) SUBDIRS=$(PWD)/ctrlplane
	
clean: 
	   rm -rf *.o *.ko *.mod.* .c* .t* 

# LOGGING: ---------------------------------------------------------------
# Add to EXTRA_CFLAGS, based on the logging level desired:
# -DDEBUG_NORMAL: To enable normal logging levels 
# -DDEBUG_VERBOSE: To enable verbose logging levels 
