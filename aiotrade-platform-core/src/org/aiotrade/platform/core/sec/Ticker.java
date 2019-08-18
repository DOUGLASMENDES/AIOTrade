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
package org.aiotrade.platform.core.sec;

import org.aiotrade.math.timeseries.datasource.TimeValue;

/**
 *
 * This is just a lightweight value object. So, it can be used to lightly store
 * tickers at various time. That is, you can store many many tickers for same
 * symbol efficiently, as in case of composing an one minute ser.
 *
 * The TickerSnapshot will present the last current snapshot ticker for one
 * symbol, and implement Observable. You only need one TickerSnapshot for each
 * symbol.
 *
 * @author Caoyuan Deng
 */
public final class Ticker implements TimeValue, Cloneable {
    public final static int LAST_PRICE = 0;
    public final static int DAY_CHANGE = 1;
    public final static int BID_PRICE = 2;
    public final static int BID_SIZE = 3;
    public final static int ASK_PRICE = 4;
    public final static int ASK_SIZE = 5;
    public final static int DAY_OPEN = 6;
    public final static int DAY_HIGH = 7;
    public final static int DAY_LOW = 8;
    public final static int PREV_CLOSE = 9;
    public final static int DAY_VOLUME = 10;
    
    private final float[] values = new float[11];
    
    private long time;
    
    public Ticker() {
    }
    
    public final void setTime(long time) {
        this.time = time;
    }
    
    public final long getTime() {
        return time;
    }
    
    public float get(int field) {
        return values[field];
    }
    
    public void set(int field, float value) {
        this.values[field] = value;
    }
    
    public final void reset() {
        time = 0;
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
    }
    
    public final void copy(Ticker another) {
        time = another.time;
        System.arraycopy(another.values, 0, this.values, 0, this.values.length);
    }
    
    public final boolean isValueChanged(Ticker another) {
        for (int i = 0; i < values.length; i++) {
            if (this.values[i] != another.values[i]) {
                return true;
            }
        }
        
        return false;
    }
    
    public final boolean isDayVolumeGrown(Ticker previousTicker) {
        return this.values[DAY_VOLUME] > previousTicker.values[DAY_VOLUME];
    }
    
    public final boolean isDayVolumeChanged(Ticker previousTicker) {
        return this.values[DAY_VOLUME] != previousTicker.values[DAY_VOLUME];
    }
    
    public final float getChangeInPercent() {
        return values[PREV_CLOSE] == 0 ? 0 : (values[LAST_PRICE] - values[PREV_CLOSE]) / values[PREV_CLOSE] * 100;
    }
    
    public final int compareLastCloseTo(Ticker previousTicker) {
        return values[LAST_PRICE] > previousTicker.values[Ticker.LAST_PRICE] ? 1 : 
            values[LAST_PRICE] == previousTicker.values[Ticker.LAST_PRICE] ? 0 : 1;
    }
    
    public final Ticker clone() {
        try {
            return (Ticker)super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
}
