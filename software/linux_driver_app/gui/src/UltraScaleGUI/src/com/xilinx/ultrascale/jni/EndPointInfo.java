/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xilinx.ultrascale.jni;

/**
 *
 * @author testadvs
 */
public class EndPointInfo {

    int MAX_BARS = 6;
    public int designMode;
    public int barmask;
    public BarInfo[] barList = new BarInfo[MAX_BARS];

    public EndPointInfo() {
    }

    public void setEndPointInfo(int[] einfo, long[][] binfo) {
        designMode = einfo[0];
        barmask = einfo[1];

        for (int i = 0; i < MAX_BARS; ++i) {
            barList[i] = new BarInfo();
            barList[i].setBarInfo(binfo[i][0], binfo[i][1]);
        }
    }

    @SuppressWarnings("empty-statement")
    public Object[][] getBarStats() {
        int count = 0;
        for (int i = 0; i < 6; i++) {
            int k = barmask & (1 << i);
            if (k > 0) {
                count ++;
            }
        }
        count = (count *4)-1;
        Object[][] bstats = new Object[count][2];
        int incVal = 0;
        for (int i = 0; i < 6; i++) {
            int k = barmask & (1 << i);
            if (k > 0) {
                if (incVal != 0) {
                    bstats[incVal][0] = "";
                    bstats[incVal][1] = "";
                    incVal++;
                }

                bstats[incVal][0] = "BAR";
                bstats[incVal][1] = i;

                bstats[incVal + 1][0] = "Virt Addr";
                bstats[incVal + 1][1] = "0x" + Long.toHexString(barList[i].barAddress).toUpperCase();

                bstats[incVal + 2][0] = "Size";
                bstats[incVal + 2][1] = barList[i].barSize / 1024 + "K";

                incVal += 3;
            }
        }

//        Object[][] bstats = 
//        {
//            {"BAR", "0"},
//            {"Virt Addr", "0x"+Long.toHexString(barList[0].barAddress).toUpperCase()},
//            {"Size", barList[0].barSize/1024+"K"},
//            {"", ""}
//                ,
//            {"BAR", "2"},
//            {"Virt Addr", "0x"+Long.toHexString(barList[2].barAddress).toUpperCase()},
//            {"Size", barList[2].barSize/1024+"K"},
//            {"", ""},
//            {"BAR", "4"},
//            {"Virt Addr", "0x"+Long.toHexString(barList[4].barAddress).toUpperCase()},
//            {"Size", barList[4].barSize/1024+"K"}
//        };
        return bstats;
    }
}
