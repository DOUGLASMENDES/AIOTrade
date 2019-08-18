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
package org.aiotrade.util.swing.action;

import java.util.ArrayList;
import java.util.Collection;
import javax.swing.Action;

/**
 *
 * @author Caoyuan Deng
 */
public class WithActionsHelper {
    private final static Collection<Action> EMPTY_ACTIONS = new ArrayList<Action>(0);
    
    private final WithActions wrapper;
    private Collection<Action> actions;
    private boolean defaultActionsAdded;
    
    public WithActionsHelper(WithActions wrapper) {
        this.wrapper = wrapper;
    }
    
    public final Action addAction(Action action) {
        if (actions == null) {
            actions = new ArrayList<Action>();
        }
        
        actions.add(action);
        return action;
    }
    
    @SuppressWarnings("unchecked")
    public final <T extends Action> T lookupAction(Class<T> type) {
        if (! defaultActionsAdded) {
            addDefaultActions();
            defaultActionsAdded = true;
        }
        
        if (actions != null) {
            for (Action action : actions) {
                if (type.isInstance(action)) {
                    return (T)action;
                }
            }
        }
        return null;
    }
    
    private final void addDefaultActions() {
        for (Action action : wrapper.createDefaultActions()) {
            addAction(action);
        }
    }
    
    public Collection<Action> getActions() {
        return actions == null ? EMPTY_ACTIONS : actions;
    }
    
}

