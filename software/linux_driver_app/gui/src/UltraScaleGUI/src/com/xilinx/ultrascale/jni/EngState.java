/*******************************************************************************
** © Copyright 2012 - 2013 Xilinx, Inc. All rights reserved.
** This file contains confidential and proprietary information of Xilinx, Inc. and 
** is protected under U.S. and international copyright and other intellectual property laws.
*******************************************************************************
**   ____  ____ 
**  /   /\/   / 
** /___/  \  /   Vendor: Xilinx 
** \   \   \/    
**  \   \
**  /   /          
** /___/    \
** \   \  /  \   Virtex-7XT PCIe-DMA-DDR3-10GMAC-10GBASER Targeted Reference Design
**  \___\/\___\
** 
**  Device: xc7k325t
**  Version: 1.0
**  Reference: UG927
**     
*******************************************************************************
**
**  Disclaimer: 
**
**    This disclaimer is not a license and does not grant any rights to the materials 
**    distributed herewith. Except as otherwise provided in a valid license issued to you 
**    by Xilinx, and to the maximum extent permitted by applicable law: 
**    (1) THESE MATERIALS ARE MADE AVAILABLE "AS IS" AND WITH ALL FAULTS, 
**    AND XILINX HEREBY DISCLAIMS ALL WARRANTIES AND CONDITIONS, EXPRESS, IMPLIED, OR STATUTORY, 
**    INCLUDING BUT NOT LIMITED TO WARRANTIES OF MERCHANTABILITY, NON-INFRINGEMENT, OR 
**    FITNESS FOR ANY PARTICULAR PURPOSE; and (2) Xilinx shall not be liable (whether in contract 
**    or tort, including negligence, or under any other theory of liability) for any loss or damage 
**    of any kind or nature related to, arising under or in connection with these materials, 
**    including for any direct, or any indirect, special, incidental, or consequential loss 
**    or damage (including loss of data, profits, goodwill, or any type of loss or damage suffered 
**    as a result of any action brought by a third party) even if such damage or loss was 
**    reasonably foreseeable or Xilinx had been advised of the possibility of the same.


**  Critical Applications:
**
**    Xilinx products are not designed or intended to be fail-safe, or for use in any application 
**    requiring fail-safe performance, such as life-support or safety devices or systems, 
**    Class III medical devices, nuclear facilities, applications related to the deployment of airbags,
**    or any other applications that could lead to death, personal injury, or severe property or 
**    environmental damage (individually and collectively, "Critical Applications"). Customer assumes 
**    the sole risk and liability of any use of Xilinx products in Critical Applications, subject only 
**    to applicable laws and regulations governing limitations on product liability.

**  THIS COPYRIGHT NOTICE AND DISCLAIMER MUST BE RETAINED AS PART OF THIS FILE AT ALL TIMES.

*******************************************************************************/
/*****************************************************************************/
/**
 *
 * @file EngState.java  
 *
 * Author: Xilinx, Inc.
 *
 * 2007-2010 (c) Xilinx, Inc. This file is licensed uner the terms of the GNU
 * General Public License version 2.1. This program is licensed "as is" without
 * any warranty of any kind, whether express or implied.
 *
 * MODIFICATION HISTORY:
 *
 * Ver   Date     Changes
 * ----- -------- -------------------------------------------------------
 * 1.0  5/15/12  First release
 *
 *****************************************************************************/

package com.xilinx.ultrascale.jni;

/*
 * EngState Class defines the structure similar to the EngState in the driver along with four additional properties.
 */

public class EngState{

    public int Engine;                 /**< Engine Number */
    public int srcSGLBDs;                    /**< Total Number of BDs */
    public int destSGLBDs;
    public int srcStatsBD;
    public int destStatsBD;
    public int Buffers;                /**< Total Number of buffers */
    public int MinPktSize;    /**< Minimum packet size */
    public int MaxPktSize;    /**< Maximum packet size */
    public int srcErrs;                 /**< Total BD errors */
    public int destErrs;                /**< Total BD short errors - only TX BDs */
    public int internalErrs;
    public int DataMismatch;
    public int IntEnab;                /**< Interrupts enabled or not */
    public int TestMode;      /**< Current Test Mode */

    // Additional parameters from EngStats
    public int TXEnab;
    public int LBEnab;
    public int PktGenEnab;
    public int PktChkEnab;

    public EngState(){}
    public void setEngState(int[] state){
       Engine = state[0];
       srcSGLBDs = state[1];
       destSGLBDs = state[2];
       srcStatsBD = state[3];
       destStatsBD = state[4];
       Buffers = state[5];
       MinPktSize = state[6];
       MaxPktSize = state[7];
       srcErrs = state[8];
       destErrs = state[9];
       internalErrs = state[10];
       DataMismatch = state[11];
       IntEnab = state[12];
       TestMode = state[13];
       TXEnab = state[14];
       LBEnab = state[15];
       PktGenEnab = state[16];
       PktChkEnab = state[17];
    }  
}
