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
package org.aiotrade.charting.chart;

import java.awt.Color;
import org.aiotrade.charting.widget.DiamondDot;
import org.aiotrade.charting.widget.WidgetModel;
import org.aiotrade.math.timeseries.SerItem;
import org.aiotrade.math.timeseries.Var;
import org.aiotrade.charting.chart.DotChart.Model;
import org.aiotrade.charting.laf.LookFeel;
import org.aiotrade.charting.widget.HeavyPathWidget;

/**
 *
 * @author Caoyuan Deng
 */
public class DotChart extends AbstractChart<Model> {
    public final static class Model implements WidgetModel {
        Var var;
        
        public void set(Var var) {
            this.var = var;
        }
    }
    
    protected Model createModel() {
        return new Model();
    }
    
    protected void plotChart() {
        final Model model = model();
        Color color = LookFeel.getCurrent().getChartColor(getDepth());
        setForeground(color);
        
        final HeavyPathWidget heavyPathWidget = addChild(new HeavyPathWidget());
        final DiamondDot template = new DiamondDot();
        for (int bar = 1; bar <= nBars; bar += nBarsCompressed) {
            
            for (int i = 0; i < nBarsCompressed; i++) {
                final long time = tb(bar + i);
                final SerItem item = ser.getItem(time);
                
                if (item != null) {
                    final float value = item.getFloat(model.var);
                    
                    if (! Float.isNaN(value)) {
                        template.model().set(xb(bar), yv(value), wBar, false);
                        template.setForeground(color);
                        template.plot();
                        heavyPathWidget.appendFrom(template);
                    }
                }
            }
        }
        
    }
    
}


