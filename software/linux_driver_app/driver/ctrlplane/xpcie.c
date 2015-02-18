/****************************************************************************
 ****************************************************************************/
/** Copyright (C) 2014-2015 Xilinx, Inc.  All rights reserved.
 ** Permission is hereby granted, free of charge, to any person obtaining
 ** a copy of this software and associated documentation files (the
 ** "Software"), to deal in the Software without restriction, including
 ** without limitation the rights to use, copy, modify, merge, publish,
 ** distribute, sublicense, and/or sell copies of the Software, and to
 ** permit persons to whom the Software is furnished to do so, subject to
 ** the following conditions:
 ** The above copyright notice and this permission notice shall be included
 ** in all copies or substantial portions of the Software.Use of the Software 
 ** is limited solely to applications: (a) running on a Xilinx device, or 
 ** (b) that interact with a Xilinx device through a bus or interconnect.  
 ** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 ** EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 ** MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 ** NONINFRINGEMENT. IN NO EVENT SHALL THE X CONSORTIUM BE LIABLE FOR ANY
 ** CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 ** TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 ** SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ** Except as contained in this notice, the name of the Xilinx shall
 ** not be used in advertising or otherwise to promote the sale, use or other
 ** dealings in this Software without prior written authorization from Xilinx
 **/
/*****************************************************************************
*****************************************************************************/

#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/pci.h>
#include <linux/init.h>
#include <linux/cdev.h>
#include <asm/uaccess.h>
#include <linux/version.h>
#include <linux/fs.h>
#include <linux/spinlock.h>

#include <xpmon_be.h>
#include <xbasic_types.h>
#include <xio.h>
#include <xstatus.h>

#include "../include/xdebug.h"
/************************** Constant Definitions *****************************/

/** @name Macros for PCI probing
 * @{
 */
#define XPCI_VENDOR_ID   0x10EE      /**< Vendor ID - Xilinx */
#define XPCI_DEVICE_ID   0x8011     /**< Xilinx's Device ID */

/** Driver information */
#define DRIVER_NAME         "xdma_ctrl_plane_driver"
#define DRIVER_DESCRIPTION  "Xilinx DMA Linux driver"
#define DRIVER_VERSION      "1.0"

#define MAX_BARS    6


/** PVTMON Macros [Voltage and temperature monitoring registers]*/

#define		PVTMON_VCCINT 			0x040
#define 	PVTMON_VCCAUX 			0x044
#define		PVTMON_VCC3 			0x048
#define 	PVTMON_VADJ 			0x04C
#define		PVTMON_VCC1 			0x050
#define 	PVTMON_VCC2 			0x054
#define 	PVTMON_MGT_AVCC 		0x058
#define 	PVTMON_MGT_AVTT 		0x05C
#define 	PVTMON_VCCAUX_IO 		0x060
#define 	PVTMON_VCCBRAM 			0x064
#define 	PVTMON_MGT_VCCAUX		0x068
#define 	PVTMON_VCC1_8			0x06C
#define		PVTMON_TEMP			0x070


#define REG_BRDG_BASE		 	0x00008000 /**< bridge base register base */
#define REG_BRDG_E_BASE          	0x00000200 /**< bridge egress  register base */

#define OFFSET_BRDG_E_CAP          	0x00000000 /**< bridge egress  Capability offset  */
#define OFFSET_BRDG_E_STATS      	0x00000004 /**< bridge egress status register offset  */
#define OFFSET_BRDG_E_CTRL        	0x00000008  /**< bridge egress control register offset*/
#define OFFSET_BRDG_E_SRC_LO    	0x00000010 /**< bridge egress source base address low register */
#define OFFSET_BRDG_E_SRC_HI      	0x00000014 /**< bridge egress source base address high register */



#define REG_INGR_AXI_BASE 		0x00000800 /**< Ingress AXI transalation base register */

#define OFFSET_INGR_AXI_CTRL		0x00000008 /**< Ingress AXI translation Control Offser */
#define OFFSET_INGR_AXI_SRC_LO   	0x00000010 /**< Ingress AXI transaltion Source Base low */
#define OFFSET_INGR_AXI_SRC_HI   	0x00000014 /**< Ingress AXI transaltion Source Base High */
#define OFFSET_INGR_AXI_DST_LO   	0x00000018 /**< Ingress AXI transaltion Destination  Base low */

#define  SECOND_TRANS 			0x20




/** Driver Module information */
MODULE_AUTHOR("Xilinx, Inc.");
MODULE_DESCRIPTION(DRIVER_DESCRIPTION);
MODULE_VERSION(DRIVER_VERSION);
MODULE_LICENSE("GPL");

/** PCI device structure which probes for targeted design */
static struct pci_device_id ids[] = {
	{ XPCI_VENDOR_ID,   XPCI_DEVICE_ID,
		PCI_ANY_ID,       PCI_ANY_ID,
		0,            0,  0UL },
	{ }     /* terminate list with empty entry */
};

/**
 * Macro to export pci_device_id to user space to allow hot plug and
 * module loading system to know what module works with which hardware device
 */
MODULE_DEVICE_TABLE(pci, ids);

/*@}*/

/************************** Function Prototypes ******************************/
static int /*__devinit */ xpcie_probe(struct pci_dev *pdev, const struct pci_device_id *ent);
static void /*__devexit */  xpcie_remove(struct pci_dev *pdev);
static void ReadConfig(struct pci_dev *);
#ifdef USE_LATER
static void ReadUserReg(u32 baseaddr);
#endif
static void InitBridge(u64 bar0_addr, u64 bar0_addr_p, u64 bar2_addr, u64 bar2_addr_p, u64 bar4_addr_p);






/** Driver information */
static struct pci_driver xpcie_driver = {
	.name = DRIVER_NAME,
	.id_table = ids,
	.probe = xpcie_probe,
	.remove = /*__devexit_p */ (xpcie_remove)
};
struct privData {
	struct pci_dev * pdev;          /**< PCI device entry */

	/** BAR information discovered on probe. BAR0 is understood by this driver.
	 * Other BARs will be used as app. drivers register with this driver.
	 */
	u32 barMask;                    /**< Bitmask for BAR information */
	struct {
		unsigned long basePAddr;/**< Physical Address of BAR */
		unsigned long baseLen;  /**< BAR length */
		void __iomem *baseVAddr;/**< Virtual address of BAR */
	} barInfo[MAX_BARS];

};

/* for exclusion of all program flows (processes, ISRs and BHs) */
static DEFINE_SPINLOCK(DmaStatsLock);

int UserOpen=0;
PowerMonitorVal pmval;
struct cdev * xdmaCdev=NULL;

struct privData * dmaData = NULL;

struct timer_list stats_timer;


static int ReadPCIState(struct pci_dev * pdev, PCIState * pcistate)
{
	int pos;
	u16 valw;
	u8 valb;
	int reg=0,linkUpCap=0;

	/* Since probe has succeeded, indicates that link is up. */
	pcistate->LinkState = LINK_UP;
	pcistate->VendorId = XPCI_VENDOR_ID;
	pcistate->DeviceId = XPCI_DEVICE_ID;

	/* Read Interrupt setting - Legacy or MSI/MSI-X */
	pci_read_config_byte(pdev, PCI_INTERRUPT_PIN, &valb);
	if(!valb)
	{
		if(pci_find_capability(pdev, PCI_CAP_ID_MSIX))
			pcistate->IntMode = INT_MSIX;
		else if(pci_find_capability(pdev, PCI_CAP_ID_MSI))
			pcistate->IntMode = INT_MSI;
		else
			pcistate->IntMode = INT_NONE;
	}
	else if((valb >= 1) && (valb <= 4))
		pcistate->IntMode = INT_LEGACY;
	else
		pcistate->IntMode = INT_NONE;

	if((pos = pci_find_capability(pdev, PCI_CAP_ID_EXP)))
	{
		/* Read Link Status */
		pci_read_config_word(pdev, pos+PCI_EXP_LNKSTA, &valw);
		pcistate->LinkSpeed = (valw & 0x0003);
		pcistate->LinkWidth = (valw & 0x03f0) >> 4;
		linkUpCap= (reg>>4) & 0x1;
		pcistate->LinkUpCap = linkUpCap;

		/* Read MPS & MRRS */
		pci_read_config_word(pdev, pos+PCI_EXP_DEVCTL, &valw);
		pcistate->MPS = 128 << ((valw & PCI_EXP_DEVCTL_PAYLOAD) >> 5);
		pcistate->MRRS = 128 << ((valw & PCI_EXP_DEVCTL_READRQ) >> 12);
	}
	else
	{
		printk("Cannot find PCI Express Capabilities\n");
		pcistate->LinkSpeed = pcistate->LinkWidth = 0;
		pcistate->MPS = pcistate->MRRS = 0;
	}


	return 0;
}


void ReadBarInfo(struct pci_dev * pdev,EndpointInfo * EndInfo)
{
	int i=0;
	EndInfo->designMode = 1;

	EndInfo->barmask = dmaData->barMask;
	for(i=0;i<MAX_BARS;i++)
	{
		EndInfo->BarList[i].BarAddress=(unsigned long)dmaData->barInfo[i].baseVAddr;
		EndInfo->BarList[i].BarSize = dmaData->barInfo[i].baseLen;
	}


}

/* Character device file operations */
static int xdma_dev_open(struct inode * in, struct file * filp)
{


	if(UserOpen)
	{
		printk("Device already in use\n");
		return -EBUSY;
	}


	spin_lock_bh(&DmaStatsLock);
	UserOpen++;                 /* To prevent more than one GUI */
	spin_unlock_bh(&DmaStatsLock);

	return 0;
}

static int xdma_dev_release(struct inode * in, struct file * filp)
{
	if(!UserOpen)
	{
		/* Should not come here */
		printk("Device not in use\n");
		return -EFAULT;
	}

	spin_lock_bh(&DmaStatsLock);
	UserOpen-- ;
	spin_unlock_bh(&DmaStatsLock);

	return 0;
}

	static ssize_t
xdma_dev_write (struct file *file,
		const char __user * buffer, size_t length, loff_t * offset)
{

	writedata wdata;
	u64 base;
	
	if(copy_from_user(&wdata, (writedata *)buffer, sizeof(writedata)))
	{
		printk("copy_from_user failed\n");
		return -1;
	}
	if( wdata.bar == 2)
	{
		base = (unsigned long)(dmaData->barInfo[2].baseVAddr);
	}
	else if(wdata.bar == 4)
	{
		base = (unsigned long)(dmaData->barInfo[4].baseVAddr);
	}
	else
	{
		printk(KERN_ERR"Un-used BAR %d \n",wdata.bar);
		return -1;
	}
	XIo_Out32(base + wdata.offset , *(wdata.bufferAddress ));	

	log_normal("Dma write Came with %d bar %x Address   %d offset %d length %x data \n", wdata.bar,wdata.bufferAddress,wdata.offset,wdata.size,*((u32 *)wdata.bufferAddress));                   
	return (wdata.size);
}


	static ssize_t
xdma_dev_read (struct file *file,
		char __user * buffer, size_t length, loff_t * offset)
{
	readdata rdata;
	int i=0;
	u32  * kbuffer;
	u32  *tempbuff;
	u64   base;


	if(copy_from_user(&rdata, (readdata *)buffer, sizeof(readdata)))
	{
		printk("copy_from_user failed\n");
		return -1;
	}
	kbuffer= kmalloc( rdata.size,GFP_KERNEL);
	tempbuff=kbuffer;

	if( rdata.bar == 2)
	{
		base  = (unsigned long)(dmaData->barInfo[2].baseVAddr);
	}
	else if (rdata.bar ==4)
	{
		base = (unsigned long)(dmaData->barInfo[4].baseVAddr);	
	}
	else
	{
		printk("Un-used BAR %d",rdata.bar);
		return -1;
	}

	for (i=0; i < ( rdata.size ) ; i=i+4)
	{
		*(tempbuff)=XIo_In32(base +( rdata.offset )+ i);
		log_normal(" %x \n",*(tempbuff));
		tempbuff++;
	}

	/* 
	 *  return the number of bytes sent , currently one or none
	 */
	log_normal("DMA read came with %d bar %x Address %d offset %d length  \n", rdata.bar,rdata.bufferAddress ,rdata.offset,rdata.size);




	if(copy_to_user( rdata.bufferAddress,kbuffer,rdata.size))
	{
		printk("copy_to_user failed\n");
	}
	kfree(kbuffer);
	return (rdata.size);
}

#if LINUX_VERSION_CODE < KERNEL_VERSION(2,6,36)
static int xdma_dev_ioctl(struct inode * in, struct file * filp,
		unsigned int cmd, unsigned long arg)
#else
static long xdma_dev_ioctl(struct file * filp,
		unsigned int cmd, unsigned long arg)
#endif
{
	int retval=0;
	PCIState pcistate;
	PowerMonitorVal pmval_temp;
	EndpointInfo End_Bar;


	/* Check cmd type and value */
	if(_IOC_TYPE(cmd) != XPMON_MAGIC) return -ENOTTY;
	if(_IOC_NR(cmd) > XPMON_MAX_CMD) return -ENOTTY;

	/* Check read/write and corresponding argument */
	if(_IOC_DIR(cmd) & _IOC_READ)
		if(!access_ok(VERIFY_WRITE, (void *)arg, _IOC_SIZE(cmd)))
			return -EFAULT;
	if(_IOC_DIR(cmd) & _IOC_WRITE)
		if(!access_ok(VERIFY_READ, (void *)arg, _IOC_SIZE(cmd)))
			return -EFAULT;
	/* Looks ok, let us continue */

	switch(cmd)
	{

		case IGET_PCI_STATE:
			ReadPCIState(dmaData->pdev, &pcistate);
			if(copy_to_user((PCIState *)arg, &pcistate, sizeof(PCIState)))
			{
				printk("copy_to_user failed\n");
				retval = -EFAULT;
				break;
			}
			break;

		case IGET_PMVAL:
			spin_lock_bh(&DmaStatsLock);
			memcpy(&pmval_temp,&pmval,sizeof(PowerMonitorVal));
			spin_unlock_bh(&DmaStatsLock);
			if(copy_to_user((PowerMonitorVal *)arg, &pmval_temp, sizeof(PowerMonitorVal)))
			{
				printk("PMVAL copy_to_user failed\n");
				retval = -EFAULT;
			}
			break;
		case IGET_BARINFO:
			ReadBarInfo(dmaData->pdev,&End_Bar);
			if(copy_to_user((PCIState *)arg, &End_Bar, sizeof(EndpointInfo)))
			{
				printk("copy_to_user failed\n");
				retval = -EFAULT;
				break;
			}
			break;


		default:
			printk("Invalid command %d \n", cmd);
			return -ENOTTY;
	//		break;
	}

	return retval;
}


/********************************************************************/
/*
 * Poll function for polling PVTMON,DMA and PCIe Stats
 */
static void poll_stats(unsigned long __opaque)
{
	struct pci_dev *pdev = (struct pci_dev *)__opaque;
	struct privData *lp;
	int  offset = 0;
	u64 base;
	lp = pci_get_drvdata(pdev);
	base = (unsigned long)(dmaData->barInfo[2].baseVAddr);
	spin_lock(&DmaStatsLock);



	pmval.vcc =XIo_In32(base + PVTMON_VCCINT);
	pmval.vccaux = XIo_In32(base + PVTMON_VCCAUX);
	pmval.vcc3v3 = XIo_In32(base + PVTMON_VCC3);
	pmval.vadj = XIo_In32(base + PVTMON_VADJ);
	pmval.vcc2v5 = XIo_In32(base + PVTMON_VCC2);
	pmval.vcc1v5 =  XIo_In32(base + PVTMON_VCC1);
	pmval.mgt_avcc =  XIo_In32(base + PVTMON_VCC1);
	pmval.mgt_avtt = XIo_In32(base + PVTMON_MGT_AVTT); 
	pmval.vccaux_io =  XIo_In32(base + PVTMON_VCCAUX_IO);
	pmval.vccbram =  XIo_In32(base + PVTMON_VCCBRAM);
	pmval.mgt_vccaux = XIo_In32(base + PVTMON_VCCAUX); 
	pmval.die_temp = XIo_In32(base + PVTMON_TEMP);


	spin_unlock(&DmaStatsLock);



	/* Reschedule poll routine */
	offset = -3;
	stats_timer.expires = jiffies + HZ + offset;
	add_timer(&stats_timer);
}

/*  PCI probing function */
/********************************************************************/
static int /*__devinit */ xpcie_probe(struct pci_dev *pdev, const struct pci_device_id *ent)
{
	int pciRet,chrRet;
	int i;
	dev_t xdmaDev;
	static struct file_operations xdmaDevFileOps;

	/* Initialize device before it is used by driver. Ask low-level
	 * code to enable I/O and memory. Wake up the device if it was
	 * suspended. Beware, this function can fail.
	 */
	pciRet = pci_enable_device(pdev);
	if (pciRet < 0)
	{
		printk(KERN_ERR "PCI device enable failed.\n");
		return pciRet;
	}
	spin_lock_init(&DmaStatsLock);

	dmaData = kmalloc(sizeof(struct privData), GFP_KERNEL);
	if(dmaData == NULL)
	{
		printk(KERN_ERR "Unable to allocate DMA private data.\n");
		pci_disable_device(pdev);
		return XST_FAILURE;
	}
	//log_verbose("dmaData at %p\n", dmaData);
	dmaData->barMask = 0;


	/*
	 * Enable bus-mastering on device. Calls pcibios_set_master() to do
	 * the needed architecture-specific settings.
	 */
	pci_set_master(pdev);

	/* Reserve PCI I/O and memory resources. Mark all PCI regions
	 * associated with PCI device as being reserved by owner. Do not
	 * access any address inside the PCI regions unless this call returns
	 * successfully.
	 */
	pciRet = pci_request_regions(pdev, DRIVER_NAME);
	if (pciRet < 0) {
		printk(KERN_ERR "Could not request PCI regions.\n");
		kfree(dmaData);
		pci_disable_device(pdev);
		return pciRet;
	}



	/* First read all the BAR-related information. 
	 * Map the BAR region to the system only when it is needed.
	 */
	for(i=0; i<MAX_BARS; i++) {
		u32 size;

		/* Atleast BAR0 must be there. */
		if ((size = pci_resource_len(pdev, i)) == 0) {
			if (i == 0) {
				printk(KERN_ERR "BAR 0 not valid, aborting.\n");
				pci_release_regions(pdev);
				kfree(dmaData);
				pci_disable_device(pdev);
				return XST_FAILURE;
			}    
			else
				continue;
		}
		else
			(dmaData->barMask) |= ( 1 << i );

		/* Check all BARs for memory-mapped or I/O-mapped. The driver is
		 * intended to be memory-mapped.
		 */
		if (!(pci_resource_flags(pdev, i) & IORESOURCE_MEM)) {
			printk(KERN_ERR "BAR %d is of wrong type, aborting.\n", i);
			pci_release_regions(pdev);                                         
			kfree(dmaData);
			pci_disable_device(pdev);
			return XST_FAILURE;
		}


		dmaData->barInfo[i].basePAddr = pci_resource_start(pdev, i);
		dmaData->barInfo[i].baseLen = size;


		/* Map bus memory to CPU space. The ioremap may fail if size
		 * requested is too long for kernel to provide as a single chunk
		 * of memory, especially if users are sharing a BAR region. In
		 * such a case, call ioremap for more number of smaller chunks
		 * of memory. Or mapping should be done based on user request
		 * with user size. Neither is being done now - maybe later.
		 */
		if((dmaData->barInfo[i].baseVAddr = ioremap((dmaData->barInfo[i].basePAddr), size)) == 0UL)
		{
			printk(KERN_ERR "Cannot map BAR %d space, invalidating.\n", i);
			(dmaData->barMask) &= ~( 1 << i );
		}    
		else
			printk(KERN_INFO "[BAR %d] Base PA %lu Len %lu VA %p \n", i,
					(dmaData->barInfo[i].basePAddr),
					(dmaData->barInfo[i].baseLen),
					(dmaData->barInfo[i].baseVAddr));
	}
	printk(KERN_INFO "Bar mask is 0x%x\n", (dmaData->barMask));
	printk(KERN_INFO "DMA Base VA %p\n",(dmaData->barInfo[0].baseVAddr));


	dmaData->pdev=pdev;

	ReadConfig(pdev);
	//- Bridge initialization	
	InitBridge((u64)dmaData->barInfo[0].baseVAddr, dmaData->barInfo[0].basePAddr,(u64) dmaData->barInfo[2].baseVAddr, dmaData->barInfo[2].basePAddr, dmaData->barInfo[4].basePAddr);

	//- Scratchpad in PVTMON
	printk("Data at BAR[2] %p offset (0x04) = %x\n",dmaData->barInfo[2].baseVAddr, XIo_In32(dmaData->barInfo[2].baseVAddr + 0x04));
	XIo_Out32((dmaData->barInfo[2].baseVAddr + 0x04),0x1234beef);	
	printk("Data at BAR[2] %p offset (0x04) = %x, expected data = 0x1234beef\n", dmaData->barInfo[2].baseVAddr, XIo_In32(dmaData->barInfo[2].baseVAddr + 0x04));
	printk("Vccint (0x40) = %x\n", XIo_In32(dmaData->barInfo[2].baseVAddr + 0x40));
	printk("Vccaux (0x44) = %x\n", XIo_In32(dmaData->barInfo[2].baseVAddr + 0x44));
	printk("Vccbram (0x64) = %x\n", XIo_In32(dmaData->barInfo[2].baseVAddr + 0x64));
	//- BRAM Control Registers
	printk("Data at BAR[4] %p offset (0x00) = %x\n",dmaData->barInfo[4].baseVAddr, XIo_In32(dmaData->barInfo[4].baseVAddr + 0x00));
	XIo_Out32((dmaData->barInfo[4].baseVAddr + 0x00),0xdead00ef);	
	XIo_Out32((dmaData->barInfo[4].baseVAddr + 0x04),0xabcd1234);	
	printk("Data at BAR[4] %p offset (0x00) = %x, expected data = 0xdead00ef\n",dmaData->barInfo[4].baseVAddr, XIo_In32(dmaData->barInfo[4].baseVAddr + 0x00));
	printk("Data at BAR[4] %p offset (0x04) = %x, expected data = 0xabcd1234\n",dmaData->barInfo[4].baseVAddr, XIo_In32(dmaData->barInfo[4].baseVAddr + 0x04));

	pci_set_drvdata(pdev, dmaData);
	/* The following code is for registering as a character device driver.
	 * The GUI will use /dev/xdma_state file to read state & statistics.
	 * Incase of any failure, the driver will come up without device
	 * file support, but statistics will still be visible in the system log.
	 */
	/* First allocate a major/minor number. */
	chrRet = alloc_chrdev_region(&xdmaDev, 0, 1, "xdma_ctrl");
	if( chrRet < 0 )
		printk(KERN_ERR "Error allocating char device region\n");
	else
	{
		/* Register our character device */
		xdmaCdev = cdev_alloc();
		if(IS_ERR(xdmaCdev))
		{
			printk(KERN_ERR "Alloc error registering device driver\n");
			unregister_chrdev_region(xdmaDev, 1);
			chrRet = -1;
		}
		else
		{
			xdmaDevFileOps.owner = THIS_MODULE;
			xdmaDevFileOps.open = xdma_dev_open;
			xdmaDevFileOps.release = xdma_dev_release;
			xdmaDevFileOps.write = xdma_dev_write;
			xdmaDevFileOps.read = xdma_dev_read;
#if LINUX_VERSION_CODE < KERNEL_VERSION(2,6,36)
			xdmaDevFileOps.ioctl = xdma_dev_ioctl;
#else
			xdmaDevFileOps.unlocked_ioctl = xdma_dev_ioctl;
#endif
			xdmaCdev->owner = THIS_MODULE;
			xdmaCdev->ops = &xdmaDevFileOps;
			xdmaCdev->dev = xdmaDev;
			chrRet = cdev_add(xdmaCdev, xdmaDev, 1);
			if(chrRet < 0)
			{
				printk(KERN_ERR "Add error registering device driver\n");
				unregister_chrdev_region(xdmaDev, 1);
			}
		}
	}

	/* Now start timer */
	init_timer(&stats_timer);
	stats_timer.expires=jiffies + HZ;
	stats_timer.data=(unsigned long) pdev;
	stats_timer.function = poll_stats;
	add_timer(&stats_timer);

	printk("Xpcie probe driver completed");
	return 0;
}

/* This function initializes the AXI PCIe NWL Bridge and sets up ingress address translations (PCIe BAR to AXI address translation).
   The AXI address is obatined from the user hardware design.	
   */
static void InitBridge(u64 bar0_addr, u64 bar0_addr_p, u64 bar2_addr, u64 bar2_addr_p, u64 bar4_addr_p)
{
	u32 reg;

	//- Read breg_cap		
	printk("Data at BAR0 offset (0x8200) = %x\n", XIo_In32(bar0_addr + REG_BRDG_BASE + REG_BRDG_E_BASE ));
	// Read breg_src_baseand setup bridge translation
	reg = XIo_In32(bar0_addr +REG_BRDG_BASE + REG_BRDG_E_BASE + OFFSET_BRDG_E_SRC_LO);
	printk("breg_src_base_lo = %0x\n", reg);
	XIo_Out32((bar0_addr + REG_BRDG_BASE + REG_BRDG_E_BASE + OFFSET_BRDG_E_SRC_LO ), (bar0_addr_p+REG_BRDG_BASE));
	reg = XIo_In32(bar0_addr + REG_BRDG_BASE + REG_BRDG_E_BASE + OFFSET_BRDG_E_SRC_LO);
	printk("breg_src_base_lo = %0x\n", reg);
	XIo_Out32((bar0_addr +REG_BRDG_BASE + REG_BRDG_E_BASE + OFFSET_BRDG_E_SRC_HI ),(u32)((bar0_addr_p +REG_BRDG_BASE ) >> 32));
	reg = XIo_In32(bar0_addr + REG_BRDG_BASE + REG_BRDG_E_BASE + OFFSET_BRDG_E_SRC_HI);
	printk("breg_src_base_hi = %0x\n", reg);

	//- Ingress Translation Region-1 to map User Reg & PVTMON
	//- Read ingress tran cap
	printk("Ingress Tran cap (0x8800) = %x\n", XIo_In32(bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE));
	//- enable translation
	reg = XIo_In32(bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE +OFFSET_INGR_AXI_CTRL);
	XIo_Out32((bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE +OFFSET_INGR_AXI_CTRL), (reg | 0x00040001));
	printk("tran_ingress_control = %x\n", XIo_In32(bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE +OFFSET_INGR_AXI_CTRL));
	//- Program src address to be BAR[2]
	XIo_Out32((bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE +OFFSET_INGR_AXI_SRC_LO), bar2_addr_p);
	printk("tran_src_lo = %x\n", XIo_In32(bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE +OFFSET_INGR_AXI_SRC_LO));
	XIo_Out32((bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE + OFFSET_INGR_AXI_SRC_HI), (u32)((bar2_addr_p)>> 32));
	printk("tran_src_hi = %x\n", XIo_In32(bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE + OFFSET_INGR_AXI_SRC_HI));
	//- Program DST address to be AXI domain address for user reg module
	XIo_Out32((bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE +OFFSET_INGR_AXI_DST_LO), 0x44A00000);
	printk("tran_dst_lo = %x\n", XIo_In32(bar0_addr +  REG_BRDG_BASE + REG_INGR_AXI_BASE +OFFSET_INGR_AXI_DST_LO));

	//- Ingress Translation Region-2 to map Block RAM Controller 
	//- Read ingress tran cap
	printk("Ingress Tran cap (0x8820) = %x\n", XIo_In32(bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS ));
	//- enable translation
	reg = XIo_In32(bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_CTRL);
	XIo_Out32((bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_CTRL), (reg | 0x00000001));
	printk("tran_ingress_control = %x\n", XIo_In32(bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_CTRL  ));
	//- Program src address to be BAR[4]
	XIo_Out32((bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_SRC_LO), bar4_addr_p);
	printk("tran_src_lo = %x\n", XIo_In32(bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_SRC_LO));
	XIo_Out32((bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_SRC_HI), (u32)((bar2_addr_p)>> 32));
	printk("tran_src_hi = %x\n", XIo_In32(bar0_addr + REG_BRDG_BASE + SECOND_TRANS + REG_INGR_AXI_BASE + OFFSET_INGR_AXI_SRC_HI));
	//- Program DST address to be AXI domain address for BRAM Controller
	XIo_Out32((bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_DST_LO ), 0xC0000000);
	//- Change this to map to 0xD000_0000 to test user extension flow
	//XIo_Out32((bar0_addr +REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_DST_LO ), 0xD0000000);
	printk("tran_dst_lo = %x\n", XIo_In32(bar0_addr + REG_BRDG_BASE + REG_INGR_AXI_BASE + SECOND_TRANS +OFFSET_INGR_AXI_DST_LO));


	reg = XIo_In32(bar0_addr +REG_BRDG_BASE + REG_BRDG_E_BASE + OFFSET_BRDG_E_CTRL ); 
	//- Enable bridge translation
	printk("e_breg_control= %0x\n", reg);
	XIo_Out32((bar0_addr + REG_BRDG_BASE + REG_BRDG_E_BASE + OFFSET_BRDG_E_CTRL), 0x00040001);
	printk("e_breg_control= %0x\n", reg);
	printk("DMA Channel 0 %x chann 1 %x ",XIo_In32(bar0_addr +0x7c),XIo_In32(bar0_addr+0x80 +0x7c) );

}

//- Placeholder to access BAR mapped registers through driver
// - Pass appropriate BAR base address
#ifdef USE_LATER
static void ReadUserReg(u32 baseaddr)
{
	u32 reg;
	u32 ii;
	//- Following depicts a write operation
	XIo_Out32((baseaddr + 0x3C),0x1234beef);	
	//- Following depicts read operation	
	printk("Data at BAR offset (0x3C) = %x\n", XIo_In32(baseaddr + 0x3C));
}
#endif
static void ReadConfig(struct pci_dev * pdev)
{
	u16 valw;

	/* Read Vendor ID */
	pci_read_config_word(pdev,PCI_VENDOR_ID,&valw);
	printk("Vendor ID : 0x%x, ", valw);

	/* Read Device ID */
	pci_read_config_word(pdev,PCI_DEVICE_ID,&valw);
	printk("Device ID : 0x%x, ", valw);

	/* Read Command Reg */
	pci_read_config_word(pdev,PCI_COMMAND,&valw);
	printk("Command Reg : 0x%x, ", valw);

	printk("\n");
}

static void /* __devexit */ xpcie_remove(struct pci_dev *pdev)
{
	int i=0;
	/* Stop the polling routines */
	spin_lock_bh(&DmaStatsLock);
	del_timer_sync(&stats_timer);
	spin_unlock_bh(&DmaStatsLock);

	for(i=0; i<MAX_BARS; i++) 
	{
		if((dmaData->barMask) & ( 1 << i ))
			iounmap(dmaData->barInfo[i].baseVAddr);
	}
	if(xdmaCdev != NULL)
	{
		printk("Unregistering char device driver\n");
		cdev_del(xdmaCdev);
		unregister_chrdev_region(xdmaCdev->dev, 1);
	}
	printk(KERN_INFO "PCI release regions and disable device.\n");
	pci_release_regions(pdev);
	pci_disable_device(pdev);
	pci_set_drvdata(pdev, NULL);
	if(dmaData != NULL)
	{
		kfree(dmaData);
	}
}

static int __init xpcie_init(void)
{
	/* Register driver */
	printk("XPCIe: Inserting Xilinx PCIe driver in kernel.\n");
	return pci_register_driver(&xpcie_driver);
}

static void __exit xpcie_cleanup(void)
{
	/* Then, unregister driver with PCI in order to free up resources */
	pci_unregister_driver(&xpcie_driver);
	printk("XPCIe: Removing Xilinx PCIe driver from kernel.\n");
}

module_init(xpcie_init);
module_exit(xpcie_cleanup);
