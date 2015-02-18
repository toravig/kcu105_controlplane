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
/*****************************************************************************/
/**
 *
 * @file com_xilinx_ultrascale_jni_DriverInfoGen.cpp  
 *
 * Author: Xilinx, Inc.
 *
 * 2014-2015 (c) Xilinx, Inc. This file is licensed uner the terms of the GNU
 * General Public License version 2.1. This program is licensed "as is" without
 * any warranty of any kind, whether express or implied.
 *
 * MODIFICATION HISTORY:
 *
 * Ver   Date     Changes
 * ----- -------- -------------------------------------------------------
 * 1.0  1/1/14  First release
 *
 *****************************************************************************/

#include <com_xilinx_ultrascale_jni_DriverInfoGen.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/stat.h>
#include <xpmon_be.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <malloc.h>
#include <errno.h>
#include <stdlib.h>


#define FILENAME "/dev/xdma_ctrl"

#define MAX_STATS 350
#define MULTIPLIER  8
#define DIVISOR     (1024*1024*1024)    /* Graph is in Gbits/s */

struct
{
    int Engine;         /* Engine number - for communicating with driver */
    //char *name;         /* Name to be used in Setup screen */
    //int mode;           /* TX/RX - incase of specific screens */
} DMAConfig[MAX_ENGS] =
{
    {0/*, LABEL1, TX_MODE*/ },
    {32/*, LABEL1, RX_MODE*/ },
    {1/*, LABEL2, TX_MODE*/ },
    {33/*, LABEL2, RX_MODE*/ }
    /*{2},
    {34},
    {3},
    {35}*/
};


jmethodID pci_callback;
jmethodID eng_callback;
jmethodID trn_callback;
jmethodID dma_callback;
jmethodID sws_callback;
jmethodID power_callback;
jmethodID log_callback;
jmethodID led_callback;
jmethodID read_callback;
jmethodID write_callback;
jmethodID readdump_callback;
jmethodID bar_callback;

int statsfd=-1; 
int StartTest(int statsfd, int engine, int testmode, int maxSize);
int StopTest(int statsfd, int engine, int testmode, int maxSize);
int getErrorCount0();
int getErrorCount1();
int getErrorCount2();
int getErrorCount3();

JNIEXPORT void JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_init(JNIEnv *env, jclass cls)
{
    // Read the driver file
    if((statsfd = open(FILENAME, O_RDWR)) < 0)
    {
        //printf("Failed to open statistics file %s\n", FILENAME);
        return;
    }
	//printf("file open id %d",statsfd);
    pci_callback = env->GetMethodID(cls, "pciStateCallback", "([I)V");
    eng_callback = env->GetMethodID(cls, "engStateCallback", "([[I)V");
    trn_callback = env->GetMethodID(cls, "trnStatsCallback", "([F)V");
    dma_callback = env->GetMethodID(cls, "dmaStatsCallback", "([[F)V");
    sws_callback = env->GetMethodID(cls, "swsStatsCallback", "([[I)V");
    power_callback = env->GetMethodID(cls, "powerStatsCallback", "([I)V");
    log_callback = env->GetMethodID(cls, "showLogCallback", "(I)V");
    led_callback = env->GetMethodID(cls, "ledStatsCallback", "([I)V");
    read_callback = env->GetMethodID(cls, "readCmdCallback", "(I)V");
    readdump_callback = env->GetMethodID(cls, "readDumpCallback", "([I)V");
    bar_callback = env->GetMethodID(cls, "barStateCallback", "([I[[J)V");
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_flush(JNIEnv *env, jobject obj){
    close(statsfd);
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_startTest(JNIEnv *env, jobject obj, jint engine, jint testmode, jint maxsize){  
        int tmode = ENABLE_LOOPBACK;
        if (testmode == 0) // loopback
           tmode = ENABLE_LOOPBACK;
        else if (testmode == 1) // checker
           tmode = ENABLE_PKTCHK;
        else if (testmode == 2) // generator
           tmode = ENABLE_PKTGEN; 
        else if (testmode == 3)
           tmode =  ENABLE_PKTCHK|ENABLE_PKTGEN;
	return StartTest(statsfd, engine, tmode, maxsize);
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_stopTest(JNIEnv *env, jobject obj, jint engine, jint testmode, jint maxsize){
        int tmode = ENABLE_LOOPBACK;
        if (testmode == 0) // loopback
           tmode = ENABLE_LOOPBACK;
        else if (testmode == 1) // checker
           tmode = ENABLE_PKTCHK;
        else if (testmode == 2) // generator
           tmode = ENABLE_PKTGEN;
        else if (testmode == 3)
           tmode =  ENABLE_PKTCHK|ENABLE_PKTGEN;
        return StopTest(statsfd, engine, tmode, maxsize);
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1DMAStats(JNIEnv *env, jobject obj){
    int j,i;
    
    EngStatsArray es;
    DMAStatistics ds[MAX_STATS]; 	
    es.Count = MAX_STATS;
    es.engptr = ds;

    jfloat tmp[MAX_ENGS][4];

    if(ioctl(statsfd, IGET_DMA_STATISTICS, &es) != 0)
    {
       // printf("IGET_DMA_STATISTICS on engines failed\n");
        return -1;
    }
    for(j=0; j<es.Count; j++)
    {
        int k, eng;

        /* Driver engine number does not directly map to that of GUI */
        for(k=0; k<MAX_ENGS; k++)
        {
            if(DMAConfig[k].Engine == ds[j].Engine)
                break;
        }

        if(k >= MAX_ENGS) continue;
        eng = k;
	
        tmp[k][0] = ds[j].Engine;
        //printf("j: %d OLBR: %f LWT: %f\n", j, ds[j].LBR, ds[j].LWT);
        //printf("LBR: %f\n", ((double)(ds[j].LBR) * MULTIPLIER )/DIVISOR);
        tmp[k][1] = ((double)(ds[j].LBR) / DIVISOR ) * MULTIPLIER * ds[j].scaling_factor;
        tmp[k][2] = ds[j].LAT;
        tmp[k][3] = ds[j].LWT; 
     }
     
     jfloatArray row= (jfloatArray)env->NewFloatArray(4);
     jobjectArray ret=env->NewObjectArray(MAX_ENGS, env->GetObjectClass(row), 0);

     for(i=0;i<MAX_ENGS;i++) {
    	row= (jfloatArray)env->NewFloatArray(4);
      //  printf("-------------------\n");
      //  printf("E: %f LBR: %f LAT: %f LWT: %f\n", tmp[i][0],tmp[i][1],tmp[i][2],tmp[i][3]);
     //	printf("-------------------\n");
    	env->SetFloatArrayRegion(row,0,4,tmp[i]);
    	env->SetObjectArrayElement(ret,i,row);
     }
     env->CallVoidMethod(obj, dma_callback, ret);
     return 0;	
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1TRNStats(JNIEnv *env, jobject obj){

    int j, i;
    TRNStatsArray tsa;
    TRNStatistics ts[8];

    tsa.Count = 8;
    tsa.trnptr = ts;

    jfloatArray jf;
    jf = env->NewFloatArray(2);
    jfloat tmp[2];

    if(ioctl(statsfd, IGET_TRN_STATISTICS, &tsa) != 0)
    {
        //printf("IGET_TRN_STATISTICS failed\n");
        return -1;
    }

    for(j=0; j<tsa.Count; j++)
    {
        //printf("Count after call %d", tsa.Count);
        tmp[0] = ((double)(ts[j].LTX) / DIVISOR) * MULTIPLIER * ts[j].scaling_factor;
        tmp[1] = ((double)(ts[j].LRX) / DIVISOR) * MULTIPLIER * ts[j].scaling_factor;
        //printf("LTX %2.3f LRX %2.3f\n",tmp[0],tmp[1]);   
    }

    env->SetFloatArrayRegion(jf, 0, 2, tmp); 
    env->CallVoidMethod(obj, trn_callback, jf);
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1PowerStats(JNIEnv *env, jobject obj){
   PowerMonitorVal powerMonitor;
   if(ioctl(statsfd, IGET_PMVAL, &powerMonitor) != 0)
   {
        printf("IGET_PMVAL failed %d\n",errno);
        return -1;
   }
   jintArray ji;
   ji = env->NewIntArray(9);
   jint tmp[9];

   tmp[0] = powerMonitor.vcc;
   tmp[1] = powerMonitor.vccaux;
   tmp[2] = powerMonitor.mgt_avcc;
   tmp[3] = powerMonitor.vccbram;
   tmp[4] = powerMonitor.die_temp;
   tmp[5] = getErrorCount0();
   tmp[6] = getErrorCount1(); 
   tmp[7] = getErrorCount2();
   tmp[8] = getErrorCount3(); 
   //printf("vcc: %d vccaux: %d temp: %d\n", tmp[0], tmp[1], tmp[4]);
   env->SetIntArrayRegion(ji, 0, 9, tmp); 
   env->CallVoidMethod(obj, power_callback, ji);
   return 0;
}


JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1SWStats (JNIEnv *env, jobject obj){

    int j, i;
    SWStatsArray ssa;
    SWStatistics ss[32];	

    ssa.Count = 32;
    ssa.swptr = ss;

    jint tmp[32][2];

    if(ioctl(statsfd, IGET_SW_STATISTICS, &ssa) != 0)
    {
        //printf("IGET_SW_STATISTICS failed\n");
        return -1;
    }	
    for(j=0; j<ssa.Count; j++)
    {
        int k, eng;

        /* Driver engine number does not directly map to that of GUI */
        for(k=0; k<MAX_ENGS; k++)
        {
            if(DMAConfig[k].Engine == ss[j].Engine)
                break;
        }

        if(k >= MAX_ENGS) continue;
        eng = k;
        tmp[j][0] = ss[j].Engine;
        tmp[j][1] = ss[j].LBR;
    }
}


JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1EngineState(JNIEnv *env, jobject obj)
{
    if (statsfd == -1)
        return -2;

    int i;
    EngState EngInfo;
    int state;
  	
    jint tmp[MAX_ENGS][14]; 		
    /* Get the state of all the engines */
    for(i=0; i<MAX_ENGS; i++)
    {
        EngInfo.Engine = DMAConfig[i].Engine;
        if(ioctl(statsfd, IGET_ENG_STATE, &EngInfo) != 0)
        {
            //printf("IGET_ENG_STATE on Engine %d failed\n", EngInfo.Engine);
            for (int k = 0; k < 14; ++k)
               tmp[i][k] = 0;
        }
        else{
	    unsigned int testmode;
            tmp[i][0] = EngInfo.Engine;
            tmp[i][1] = EngInfo.SrcSglBD;
            tmp[i][2] = EngInfo.DstSglBD;
            tmp[i][3] = EngInfo.SrcStatsBD;
            tmp[i][4] = EngInfo.DstStatsBD;
            tmp[i][5] = EngInfo.Buffers;
            tmp[i][6] = EngInfo.MinPktSize;
            tmp[i][7] = EngInfo.MaxPktSize;
            tmp[i][8] = EngInfo.SrcErrors;
            tmp[i][9] = EngInfo.DstErrors;    
	    tmp[i][10] = EngInfo.IntErrors;
            tmp[i][11] = EngInfo.DataMismatch;
            tmp[i][12] = EngInfo.IntEnab;		

            // These additional ones are for EngStats structure
            testmode = EngInfo.TestMode;
            tmp[i][13]= testmode;		
            state = (testmode & (TEST_START|TEST_IN_PROGRESS)) ? 1 : -1;
            tmp[i][14] = state; // EngStats[i].TXEnab
            state = (testmode & ENABLE_LOOPBACK)? 1 : -1;
            tmp[i][15] = state; // EngStats[i].LBEnab
            state = (testmode & ENABLE_PKTGEN)? 1 : -1;
            tmp[i][16] = state; // EngStats[i].PktGenEnab
            state = (testmode & ENABLE_PKTCHK)? 1 : -1;
            tmp[i][17] = state; //EngStats[i].PktChkEnab
        }
    }
   	
    jintArray row= (jintArray)env->NewIntArray(18);
    jobjectArray ret=env->NewObjectArray(MAX_ENGS, env->GetObjectClass(row), 0);

    for(i=0;i<MAX_ENGS;i++) {
    	row= (jintArray)env->NewIntArray(18);
    	env->SetIntArrayRegion(row,0,18,tmp[i]);
    	env->SetObjectArrayElement(ret,i,row);
    }
    env->CallVoidMethod(obj, eng_callback, ret);
    return 0; 
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_get_1PCIstate(JNIEnv *env, jobject obj)
{
     if (statsfd == -1)
        return -2;

     PCIState PCIInfo; 	
     // make ioctl call
     if(ioctl(statsfd, IGET_PCI_STATE, &PCIInfo) != 0)
     {
        printf("IGET_PCI_STATE failed\n");
        return -1;
     }
     jintArray ji;
     ji = env->NewIntArray(16);
     jint tmp[16];

     tmp[0] = PCIInfo.Version;
     tmp[1] = PCIInfo.LinkState;
     tmp[2] = PCIInfo.LinkSpeed;
     tmp[3] = PCIInfo.LinkWidth;
     tmp[4] = PCIInfo.VendorId;
     tmp[5] = PCIInfo.DeviceId;
     tmp[6] = PCIInfo.IntMode;
     tmp[7] = PCIInfo.MPS;
     tmp[8] = PCIInfo.MRRS;
     tmp[9] = PCIInfo.InitFCCplD;
     tmp[10] = PCIInfo.InitFCCplH;
     tmp[11] = PCIInfo.InitFCNPD;
     tmp[12] = PCIInfo.InitFCNPH;
     tmp[13] = PCIInfo.InitFCPD;
     tmp[14] = PCIInfo.InitFCPH; 	
     tmp[15] = PCIInfo.LinkUpCap;

     env->SetIntArrayRegion(ji, 0, 16, tmp); 
     env->CallVoidMethod(obj, pci_callback, ji);
     return 0;
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_setLinkSpeed(JNIEnv *env, jobject jobj, jint speed){
    DirectLinkChg dLink;

    dLink.LinkSpeed = 1;   // default
    dLink.LinkWidth = 1;   // default

    dLink.LinkSpeed = speed;

    if(ioctl(statsfd, ISET_PCI_LINKSPEED, &dLink) < 0)
    {
        printf("ISET_PCI_LINKSPEED failed\n");
        return -1;
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_setLinkWidth(JNIEnv *env, jobject jobj, jint width){

    DirectLinkChg dLink;

    dLink.LinkSpeed = 1;   // default
    dLink.LinkWidth = 1;   // default

    dLink.LinkWidth = width;

    if(ioctl(statsfd, ISET_PCI_LINKWIDTH, &dLink) < 0)
    {
        //printf("ISET_PCI_LINKWIDTH failed\n");
        return -1;
    }
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_LedStats(JNIEnv *env, jobject obj){
     LedStats ledStats;
     if(ioctl(statsfd, IGET_LED_STATISTICS, &ledStats) < 0)
     {
        //printf("ISET_PCI_LINKWIDTH failed\n");
        return -1;
     }	

     jintArray ji;
     ji = env->NewIntArray(6);
     jint tmp[6];
     
     tmp[0] = ledStats.DdrCalib0;
     tmp[1] = ledStats.DdrCalib1;
     tmp[2] = ledStats.Phy0; 
     tmp[3] = ledStats.Phy1; 
     tmp[4] = ledStats.Phy2; 
     tmp[5] = ledStats.Phy3; 
 
     env->SetIntArrayRegion(ji, 0, 6, tmp); 
     env->CallVoidMethod(obj, led_callback, ji);
     return 0;
}
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_ReadCmd(JNIEnv *env, jobject obj, jint vbar, jint voffset){
     readdata rData;
     rData.bufferAddress = (int*)malloc(sizeof(int));
     rData.bar = vbar;
     rData.offset = voffset;
     rData.size = sizeof(int);
     
     if(read(statsfd,&rData,sizeof(readdata)) < 0)
     {
        printf("Read failed\n");
        return -1;
     }	
     jint result = rData.bufferAddress[0];
     env->CallVoidMethod(obj, read_callback, result);

     free(rData.bufferAddress);
     
     return 0;
}
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_WriteCmd(JNIEnv *env, jobject obj, jint vbar, jint voffset, jlong vdata){
     
     writedata rData;
     rData.bufferAddress = (int*)malloc(sizeof(int));
    
     memcpy(rData.bufferAddress, &vdata, sizeof(int));
     rData.bar = vbar;
     rData.offset = voffset;
     rData.size = sizeof(int);
     
     int ret = write(statsfd,&rData,sizeof(writedata));
    
     if(ret < 0)
     {
        printf("Write failed\n");
        return -1;
     }	
     free(rData.bufferAddress);
     
     return 0;
}
JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_ReadDump(JNIEnv *env, jobject obj, jint vbar, jint voffset, jint vsize){
     readdata rData;
     rData.bufferAddress = (int*)malloc(vsize);
     rData.bar = vbar;
     rData.offset = voffset;
     rData.size = vsize;
     
     if(read(statsfd,&rData,sizeof(readdata)) < 0)
     {
        printf("Read failed\n");
        return -1;
     }	

     jintArray result;
     result = env->NewIntArray(vsize/4);
     jint tmp[1024];
     for(int i = 0; i< (vsize/4); i++){

	tmp[i] = rData.bufferAddress[i];
     }

     env->SetIntArrayRegion(result, 0, vsize/4, tmp);     
     env->CallVoidMethod(obj, readdump_callback, result);
     free(rData.bufferAddress);
     return 0;
}

JNIEXPORT jint JNICALL Java_com_xilinx_ultrascale_jni_DriverInfoGen_BarInfo(JNIEnv *env, jobject obj){
     EndpointInfo endpointInfo;
     if(ioctl(statsfd, IGET_BARINFO, &endpointInfo) < 0)
     {
        //printf("ISET_PCI_LINKWIDTH failed\n");
        return -1;
     }	
     	
     jintArray epinfo;
     epinfo = env->NewIntArray(2);
     jint tmp[2];
     
     tmp[0] = endpointInfo.designMode;
     tmp[1] = endpointInfo.barmask;
     
     jlong tmp1[MAX_BARS][2];
     int i; 
     for (i = 0; i < MAX_BARS; ++i)
     {
        if (i == 0 || i == 2 || i == 4){
          tmp1[i][0] = endpointInfo.BarList[i].BarAddress;
          tmp1[i][1] = endpointInfo.BarList[i].BarSize;
        }else{
          tmp1[i][0] = 0;
          tmp1[i][1] = 0;
        } 
     }  

     jlongArray row= (jlongArray)env->NewLongArray(2);
     jobjectArray ret=env->NewObjectArray(MAX_BARS, env->GetObjectClass(row), 0);

     for(i=0;i<MAX_BARS;i++) {
    	row= (jlongArray)env->NewLongArray(2);
    	env->SetLongArrayRegion(row,0,2,tmp1[i]);
    	env->SetObjectArrayElement(ret,i,row);
    }

     env->SetIntArrayRegion(epinfo, 0, 2, tmp); 
     env->CallVoidMethod(obj, bar_callback, epinfo, ret);
     return 0;
}

