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
package org.aiotrade.math.timeseries;

import java.util.SortedSet;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class AbstractSer implements Ser {
    
    private EventListenerList serChangeListenerList = new EventListenerList();
    
    protected Frequency freq = Frequency.DAILY;
    
    private boolean loaded;

    public AbstractSer() {
    }
    
    public AbstractSer(final Frequency freq) {
        init(freq);
    }
    
    public void init(final Frequency freq) {
        this.freq = freq.clone();
    }
    
    public final Frequency getFreq() {
        return freq;
    }
    
    public void addSerChangeListener(SerChangeListener listener) {
        serChangeListenerList.add(SerChangeListener.class, listener);
    }
    
    public void removeSerChangeListener(SerChangeListener listener) {
        serChangeListenerList.remove(SerChangeListener.class, listener);
    }
    
    public void fireSerChangeEvent(SerChangeEvent evt) {
        Object[] listeners = serChangeListenerList.getListenerList();
        /** Each listener occupies two elements - the first is the listener class */
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SerChangeListener.class) {
                ((SerChangeListener)listeners[i + 1]).serChanged(evt);
            }
        }
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public void setLoaded(boolean b) {
        this.loaded = b;
    }
}

