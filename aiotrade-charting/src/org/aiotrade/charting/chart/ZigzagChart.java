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
import java.util.List;
import org.aiotrade.charting.widget.LineSegment;
import org.aiotrade.charting.widget.WidgetModel;
import org.aiotrade.math.timeseries.Var;
import org.aiotrade.charting.chart.ZigzagChart.Model;
import org.aiotrade.charting.laf.LookFeel;
import org.aiotrade.charting.widget.HeavyPathWidget;

/**
 *
 * @author Caoyuan Deng
 */
public class ZigzagChart extends AbstractChart<Model> {
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
        final LineSegment template = new LineSegment();
        int index1 = getFirstIndexOfEffectiveValue(0);
        while (true) {
            if (index1 < 0) {
                /** found none */
                break;
            }
            
            int position1 = masterSer.rowOfTime(ser.timestamps().get(index1));
            int bar1 = br(position1);
            if (bar1 > nBars) {
                /** exceeds visible range */
                break;
            }
            
            int index2 = getFirstIndexOfEffectiveValue(index1 + 1);
            if (index2 < 0) {
                /** no more values now */
                break;
            }
            
            int position2 = masterSer.rowOfTime(ser.timestamps().get(index2));
            int bar2 = br(position2);
            if (bar2 < 1) {
                /** not in visible range yet */
                index1 = index2;
                continue;
            }
            
            /** now we've got two good positions, go on to draw a line between them */
            
            final float value1 = ser.getItem(tb(bar1)).getFloat(model.var);
            final float value2 = ser.getItem(tb(bar2)).getFloat(model.var);
            
            /** now try to draw line between these two points */
            final float x1 = xb(bar1);
            final float x2 = xb(bar2);
            final float y1 = yv(value1);
            final float y2 = yv(value2);
            
            template.setForeground(color);
            template.model().set(x1, y1, x2, y2);
            template.plot();
            heavyPathWidget.appendFrom(template);
            
            /** set new position1 for next while loop */
            index1 = index2;
        }
    }
    
    private int getFirstIndexOfEffectiveValue(int fromIndex) {
        int index = -1;
        
        List<Float> values = model().var.values();
        for (int i = fromIndex, n = values.size(); i < n; i++) {
            Float value = values.get(i);
            if (value != null && !Float.isNaN(value)) {
                index = i;
                break;
            }
        }
        
        return index;
    }
    
}
