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
package org.aiotrade.platform.modules.indicator.basic;

import org.aiotrade.math.timeseries.plottable.Plot;
import org.aiotrade.platform.core.analysis.indicator.AbstractContIndicator;
import org.aiotrade.platform.core.analysis.indicator.AbstractIndicator.DefaultOpt;
import org.aiotrade.platform.core.analysis.indicator.IndicatorName;
import org.aiotrade.math.timeseries.computable.Opt;
import org.aiotrade.math.timeseries.DefaultSer.DefaultVar;
import org.aiotrade.math.timeseries.Var;

/**
 *
 * @author Caoyuan Deng
 */
@IndicatorName("Zigzag")
public class ZIGZAGIndicator extends AbstractContIndicator {
    
    Opt percent = new DefaultOpt("Turn Persent", 0.03, 0.01);
    
    Var<Float> zigzag       = new DefaultVar("ZIGZAG", Plot.Zigzag);
    Var<Float> pseudoZigzag = new DefaultVar("PSEUDO", Plot.Zigzag);
    
    {
        _sname = "ZIGZAG";
        _lname = "Zigzag";
        _overlapping = true;
    }
    
    protected void computeCont(int begIdx) {
        for (int i = begIdx; i < _itemSize; i++) {
            zigzag.set(i, zigzag(i, percent));
            pseudoZigzag.set(i, pseudoZigzag(i, percent));
        }
    }
    
}




