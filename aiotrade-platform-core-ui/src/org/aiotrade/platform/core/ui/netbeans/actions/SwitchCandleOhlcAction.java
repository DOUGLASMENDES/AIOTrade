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
package org.aiotrade.platform.core.ui.netbeans.actions;
import java.lang.ref.WeakReference;
import javax.swing.JOptionPane;
import org.aiotrade.platform.core.analysis.chartview.AnalysisQuoteChartView;
import org.aiotrade.platform.core.analysis.chartview.RealtimeQuoteChartView;
import org.aiotrade.platform.core.ui.netbeans.windows.AnalysisChartTopComponent;
import org.aiotrade.platform.core.ui.netbeans.windows.RealtimeChartTopComponent;
import org.aiotrade.platform.core.ui.netbeans.windows.RealtimeChartsTopComponent;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


/**
 *
 * @author Caoyuan Deng
 */
public class SwitchCandleOhlcAction extends CallableSystemAction {
    
    /** Creates a new instance
     */
    public SwitchCandleOhlcAction() {
    }
    
    
    public void performAction() {
        try {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    TopComponent tc = WindowManager.getDefault().getRegistry().getActivated();
                    if (tc == null) {
                        return;
                    }
                    
                    if (tc instanceof AnalysisChartTopComponent) {
                        /** As all AnalysisQuoteChartView have the static quoteChartType, so call static method */
                        AnalysisQuoteChartView.switchAllQuoteChartType(null);
                        for (WeakReference<AnalysisChartTopComponent> ref : AnalysisChartTopComponent.getInstanceRefs()) {
                            ref.get().repaint();
                        }
                    } else if (tc instanceof RealtimeChartsTopComponent) {
                        /** As all RealtimeQuoteChartView have the static quoteChartType, so call static method */
                        RealtimeQuoteChartView.switchAllQuoteChartType(null);
                    } else {
                        JOptionPane.showMessageDialog(
                                WindowManager.getDefault().getMainWindow(), 
                                "Please select a view by clicking on it first!");
                    }
                }
            });
        } catch (Exception e) {
        }
        
    }
    
    public String getName() {
        return "Candle/Bar/Line";
    }
    
    
    
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected String iconResource() {
        return "org/aiotrade/platform/core/ui/netbeans/resources/candleOhlc.gif";
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    
}


