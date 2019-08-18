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
package org.aiotrade.charting.widget;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

/**
 *
 * @author  Caoyuan Deng
 * @version 1.0, November 27, 2006, 7:38 AM
 * @since   1.0.4
 */
public class PathWidget<M extends WidgetModel> extends AbstractWidget<M> {
    
    private final GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO);
    
    public PathWidget() {
        super();
    }
    
    protected M createModel() {
        return null;
    }
    
    public GeneralPath getPath() {
        return path;
    }
    
    @Override
    protected Rectangle makePreferredBounds() {
        final Rectangle pathBounds = path.getBounds();
        return new Rectangle(
                pathBounds.x, pathBounds.y, 
                pathBounds.width + 1, pathBounds.height + 1);
    }
    
    @Override
    protected boolean widgetContains(double x, double y, double width, double height) {
        return path.contains(x, y, width, height);
    }
    
    @Override
    protected boolean widgetIntersects(double x, double y, double width, double height) {
        return path.intersects(x, y, width, height);
    }
    
    protected void plotWidget() {
    }
    
    public void reset() {
        super.reset();
        path.reset();
    }
    
    public void renderWidget(Graphics g0) {
        final Graphics2D g = (Graphics2D)g0;
        
        g.setColor(getForeground());
        g.draw(path);
    }
}
