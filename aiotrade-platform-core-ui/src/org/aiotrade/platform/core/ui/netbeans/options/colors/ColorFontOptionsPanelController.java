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
package org.aiotrade.platform.core.ui.netbeans.options.colors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.aiotrade.charting.chart.QuoteChart;
import org.aiotrade.platform.core.analysis.chartview.AnalysisQuoteChartView;
import org.aiotrade.platform.core.analysis.chartview.RealtimeQuoteChartView;
import org.aiotrade.charting.laf.CityLights;
import org.aiotrade.charting.laf.LookFeel;
import org.aiotrade.charting.laf.Gray;
import org.aiotrade.platform.core.UserOptionsManager;
import org.aiotrade.charting.laf.White;
import org.aiotrade.platform.core.PersistenceManager;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;


/**
 * Implementation of one panel in Options Dialog.
 *
 * @author Caoyuan Deng
 */
public final class ColorFontOptionsPanelController extends OptionsPanelController {
    
    private PropertyChangeSupport pcs;
    private ColorFontOptionsPanel optionsPanel;
    private boolean changed = false;
    private String currentPropertyValue = "property value";
    
    
    public ColorFontOptionsPanelController() {
        optionsPanel = new ColorFontOptionsPanel();
        
    }
    
    public void update() {
        // init values in your panel here
        String lafStr = null;
        LookFeel laf = LookFeel.getCurrent();
        if (laf instanceof White) {
            lafStr = "White";
        } else if (laf instanceof CityLights) {
            lafStr = "City Lights";
        } else {
            lafStr = "Gray";
        }
        optionsPanel.lafBox.setSelectedItem(lafStr);
        
        optionsPanel.reverseColorBox.setSelected(LookFeel.getCurrent().isPositiveNegativeColorReversed());
        
        optionsPanel.thinVolumeBox.setSelected(LookFeel.getCurrent().isThinVolumeBar());
        
        optionsPanel.quoteChartTypeBox.setSelectedItem(LookFeel.getCurrent().getQuoteChartType());
        
        optionsPanel.antiAliasBox.setSelected(LookFeel.getCurrent().isAntiAlias());
        
        optionsPanel.autoHideScrollBox.setSelected(LookFeel.getCurrent().isAutoHideScroll());
        
        optionsPanel.initPreviewPanel();
    }
    
    /**
     * this method is called when Ok button has been pressed
     * save values here */
    public void applyChanges() {
        String lafStr = (String)optionsPanel.lafBox.getSelectedItem();
        LookFeel laf = null;
        if (lafStr.equalsIgnoreCase("White")) {
            laf = new White();
        } else if (lafStr.equalsIgnoreCase("City Lights")) {
            laf = new CityLights();
        } else {
            laf = new Gray();
        }
        LookFeel.setCurrent(laf);
        
        QuoteChart.Type type = (QuoteChart.Type)optionsPanel.quoteChartTypeBox.getSelectedItem();
        LookFeel.getCurrent().setQuoteChartType(type);
        
        LookFeel.getCurrent().setPositiveNegativeColorReversed(optionsPanel.reverseColorBox.isSelected());
        
        LookFeel.getCurrent().setThinVolumeBar(optionsPanel.thinVolumeBox.isSelected());
        
        LookFeel.getCurrent().setAntiAlias(optionsPanel.antiAliasBox.isSelected());
        
        LookFeel.getCurrent().setAutoHideScroll(optionsPanel.autoHideScrollBox.isSelected());

        PersistenceManager.getDefault().saveProperties();
        
        AnalysisQuoteChartView.switchAllQuoteChartType(type);
        RealtimeQuoteChartView.switchAllQuoteChartType(type);
        
        for (Object c : TopComponent.getRegistry().getOpened()) {
            if (c instanceof TopComponent) {
                ((TopComponent)c).repaint();
            }
        }
    }
    
    /**
     * This method is called when Cancel button has been pressed
     * revert any possible changes here
     */
    public void cancel() {
        /** enforce OptionsSystem to reload options from previous properties files */
        UserOptionsManager.setOptionsLoaded(false);
    }
    
    public boolean isValid() {
        return true;
    }
    
    public boolean isChanged() {
        return changed;
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("netbeans.optionsDialog.editor.example");
    }
    
    public JComponent getComponent(Lookup masterLookup) {
        return optionsPanel;
    }
    
    
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
    }
    
    
    
    
}