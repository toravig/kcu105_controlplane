
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
 ** NONINFRINGEMENT. IN NO EVENT SHALL XILINX BE LIABLE FOR ANY
 ** CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 ** TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 ** SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ** Except as contained in this notice, the name of the Xilinx shall
 ** not be used in advertising or otherwise to promote the sale, use or other
 ** dealings in this Software without prior written authorization from Xilinx
 **/
/*****************************************************************************
*****************************************************************************/
package com.xilinx.ultrascale.gui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author saisyam
 */
public class BarCharts {
    JFreeChart chart;
    Color bg;
    String title;
    String[] seriesLabels;
    DefaultCategoryDataset dataset;
    int index;
    int mark;
    int reverseMark;
    
    public BarCharts(String title,String type, Color bg, String[] labels){
        
        this.bg = bg;
        this.title = title;
        seriesLabels = labels;
        index = 1;
        makeChart(type);
    }
    
    public ChartPanel getChart(String title){
        ChartPanel chartpanel = new ChartPanel(chart);
        chartpanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(title),
                        BorderFactory.createRaisedBevelBorder()));
        return chartpanel;
    }
    
    private void makeChart(String name){
        dataset = new DefaultCategoryDataset();
//        chart = ChartFactory.createLineChart("", "", name, dataset, PlotOrientation.VERTICAL, true, true, false);
//        chart = ChartFactory.createBarChart("asd", "asd", "asdffa", dataset, PlotOrientation.HORIZONTAL, true, true, false);
        
                chart = ChartFactory.createBarChart(
            "power",
            "Time Interval",
            "Power (in Watt)",
            dataset,
            PlotOrientation.HORIZONTAL,
            true,
            true,
            false
        );
        chart.setBackgroundPaint(bg);
        TextTitle ttitle = new TextTitle(title, new Font(title, Font.BOLD, 15));
        ttitle.setPaint(Color.BLACK);
        chart.setTitle(ttitle);
                
        CategoryPlot plot = chart.getCategoryPlot();
//        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
//        renderer.setDrawBarOutline(false);
        ValueAxis axis = plot.getRangeAxis();
        axis.setUpperBound(1000.0);
        axis.setLowerBound(0.0);
        axis.setTickLabelPaint(new Color(0, 0, 0));//(185, 185, 185)
        axis.setLabelPaint(new Color(0, 0, 0));
        
        CategoryAxis caxis = plot.getDomainAxis();
        caxis.setTickLabelPaint(new Color(0, 0, 0));
        caxis.setLabelPaint(new Color(0, 0, 0));
        
        renderer.setItemMargin(0);
        renderer.setSeriesPaint(0, new Color(0, 0, 0x80));
        renderer.setSeriesPaint(1, new Color(0, 0x80, 0xff));
        renderer.setSeriesPaint(2, new Color(0xa2, 0x45, 0x73));
//        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{0}:{2}", new DecimalFormat("0.000")));
        //renderer.setMaximumBarWidth(0.05);
//        addDummy();
    }
    
    public void updateChart(double val1){
        String name = "";
        if (mark > 0){
            name = ""+reverseMark;
            dataset.addValue(val1, seriesLabels[0], name);
//            dataset.addValue(val2, seriesLabels[1], name);
//            dataset.addValue(val3, seriesLabels[2], name);
            mark--;
            reverseMark++;
        }else{
            if (dataset.getColumnCount() == 9){
                dataset.removeColumn(0);
            }
            name = ""+index;
            dataset.addValue(val1, seriesLabels[0], name);
//            dataset.addValue(val2, seriesLabels[1], name);
//            dataset.addValue(val3, seriesLabels[2], name); // PCI reads/write
            index++;
        }
    }
    
    private void addDummy(){
        for (int i = 0; i < 9; ++i){
            String name = ""+index;
            dataset.addValue(-1, seriesLabels[0], name);
            dataset.addValue(-1, seriesLabels[1], name);
//            dataset.addValue(-1, seriesLabels[2], name); // PCI reads/write
            index++;
        }
        mark = 9;
        reverseMark = 1;
    }
    
    public void reset(){
        dataset.clear();
        index = 1;
        addDummy();
    }
}
