/*
 * Copyright (c) 2006-2007, AIOTrade Computing Co. and Contributors
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *    
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *    
 *  o Neither the name of AIOTrade Computing Co. nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *    
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.aiotrade.platform.core.analysis.chartview;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import org.aiotrade.charting.view.ChartingController;
import org.aiotrade.charting.view.WithVolumePane;
import org.aiotrade.math.timeseries.Ser;
import org.aiotrade.charting.chart.VolumeChart;
import org.aiotrade.charting.view.pane.ChartPane;
import org.aiotrade.math.timeseries.SerChangeEvent;
import org.aiotrade.math.timeseries.Var;
import org.aiotrade.charting.chart.QuoteChart;
import org.aiotrade.math.timeseries.QuoteSer;
import org.aiotrade.charting.view.pane.Pane;
import org.aiotrade.platform.core.sec.Ticker;
import org.aiotrade.charting.laf.LookFeel;
import org.aiotrade.charting.view.ChartView;

/**
 *
 * @author Caoyuan Deng
 */
public class RealtimeQuoteChartView extends AbstractQuoteChartView implements WithVolumePane {
    
    /** all RealtimeQuoteChartView instances share the same type */
    private static QuoteChart.Type quoteChartType;
    
    private float prevClose = -1;
    
    private ChartPane volumeChartPane;
    private JComponent volumeValuePane;
    
    public RealtimeQuoteChartView() {
    }
    
    public RealtimeQuoteChartView(ChartingController controller, QuoteSer quoteSer) {
        init(controller, quoteSer);
    }
    
    @Override
    public void init(ChartingController controller, Ser mainSer) {
        super.init(controller, mainSer);
        
        quoteChartType = LookFeel.getCurrent().getQuoteChartType();
    }
    
    protected void initComponents() {
        glassPane.setUsingInstantTitleValue(true);
        
        volumeChartPane = new ChartPane(this);
        volumeChartPane.setPreferredSize(new Dimension(10, (int)(10 / 6.18)));
        
        volumeValuePane = new JComponent() {
            protected void paintComponent(Graphics g) {
                g.setColor(LookFeel.getCurrent().backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        volumeValuePane.setPreferredSize(new Dimension(AXISY_WIDTH, (int)(10 / 6.18)));
        
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = gbc.CENTER;
        gbc.fill = gbc.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 100;
        gbc.weighty = 100 - 100 / 6.18;
        add(glassPane, gbc);
        
        gbc.anchor = gbc.CENTER;
        gbc.fill = gbc.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 100;
        gbc.weighty = 100 - 100 / 6.18;
        add(mainLayeredPane, gbc);
        
        gbc.anchor = gbc.CENTER;
        gbc.fill = gbc.BOTH;
        gbc.gridx = 0;
        gbc.gridy = gbc.RELATIVE;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 100;
        gbc.weighty = 100 / 6.18;
        add(volumeChartPane, gbc);
        
        gbc.anchor = gbc.CENTER;
        gbc.fill = gbc.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 100;
        add(axisYPane, gbc);
        
        gbc.anchor = gbc.CENTER;
        gbc.fill = gbc.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 100 / 6.18;
        add(volumeValuePane, gbc);
        
        gbc.anchor = gbc.CENTER;
        gbc.fill = gbc.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = gbc.RELATIVE;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 100;
        gbc.weighty = 0;
        add(axisXPane, gbc);
        
        gbc.anchor = gbc.SOUTH;
        gbc.fill = gbc.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = gbc.RELATIVE;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 100;
        gbc.weighty = 0;
        add(divisionPane, gbc);
        
        mainChartPane.setAutoReferCursorValue(false);
        volumeChartPane.setAutoReferCursorValue(false);
        axisYPane.setAutoReferCursorValue(false);
        glassPane.setAutoReferCursorValue(false);
    }
    
    @Override
    protected void putChartsOfMainSer() {
        super.putChartsOfMainSer();
        
        VolumeChart volumeChart = new VolumeChart();
        
        Set<Var<?>> vars = new HashSet<Var<?>>();
        mainSerChartMapVars.put(volumeChart, vars);
        vars.add(getQuoteSer().getVolume());
        
        volumeChart.model().set(true);
        
        volumeChart.set(volumeChartPane, mainSer, Pane.DEPTH_DEFAULT);
        volumeChartPane.putChart(volumeChart);
    }
    
    @Override
    public void computeMaxMin() {
        super.computeMaxMin();
        float minValue = +Float.MAX_VALUE;
        float maxValue = -Float.MAX_VALUE;
        
        if (prevClose > 0) {
            minValue = Math.min(getMinValue(), prevClose * 0.9995f);
            maxValue = Math.max(getMaxValue(), prevClose * 1.0005f);
        }
        
        if (maxValue == minValue) {
            maxValue += 1;
        }
        
        setMaxValue(maxValue);
        setMinValue(minValue);
    }
    
    public ChartPane getVolumeChartPane() {
        return volumeChartPane;
    }
    
    public QuoteChart.Type getQuoteChartType() {
        return quoteChartType;
    }
    
    public void switchQuoteChartType(QuoteChart.Type type) {
        switchAllQuoteChartType(type);
        
        repaint();
    }
    
    public static void switchAllQuoteChartType(QuoteChart.Type type) {
        quoteChartType = internal_switchAllQuoteChartType(quoteChartType, type);
    }
    
    public void setPrevClose(float prevClose) {
        this.prevClose = prevClose;
        mainChartPane.setReferCursorValue(prevClose);
        glassPane.setReferCursorValue(prevClose);
    }
    
    @Override
    public void popupToDesktop() {
        ChartView popupView = new RealtimeQuoteChartView(getController(), getQuoteSer());
        popupView.setInteractive(false);
        final Dimension dimension = new Dimension(200, 150);
        final boolean alwaysOnTop = true;
        
        getController().popupViewToDesktop(popupView, dimension, alwaysOnTop, false);
    }
    
    @Override
    protected void updateView(SerChangeEvent evt) {
        Object lastTicker = evt.getLastObject();
        
        if (lastTicker != null && lastTicker instanceof Ticker) {
            Ticker ticker = (Ticker)lastTicker;
            
            float percentValue = ticker.getChangeInPercent();
            
            String strValue = String.format("%+3.2f", percentValue) + "%  " + ticker.get(Ticker.LAST_PRICE);
            
            Color color = percentValue >= 0 ?
                LookFeel.getCurrent().getPositiveColor() :
                LookFeel.getCurrent().getNegativeColor();
            
            getGlassPane().updateInstantValue(strValue, color);
            setPrevClose(ticker.get(Ticker.PREV_CLOSE));
        }
        
        super.updateView(evt);
    }
    
    
}


