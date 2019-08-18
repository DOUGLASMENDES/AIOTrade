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
package org.aiotrade.platform.core;

import org.aiotrade.platform.core.netbeans.NetBeansLogManager;

/**
 *
 * @author Caoyuan Deng
 */
public abstract class LogManager {
    
    /**
     * Undefined severity.
     * May be used only in {@link #notify(int, Throwable)}
     * and {@link #annotate(Throwable, int, String, String, Throwable, Date)}.
     */
    public static final int UNKNOWN = 0x00000000;
    
    /** Message that would be useful for tracing events but which need not be a problem. */
    public static final int INFORMATIONAL = 0x00000001;
    
    /** Something went wrong in the software, but it is continuing and the user need not be bothered. */
    public static final int WARNING = 0x00000010;
    
    /** Something the user should be aware of. */
    public static final int USER = 0x00000100;
    
    /** Something went wrong, though it can be recovered. */
    public static final int EXCEPTION = 0x00001000;
    
    /** Serious problem, application may be crippled. */
    public static final int ERROR = 0x00010000;
    
    
    private static LogManager defaultManager;
    
    public static LogManager getDefault() {
        if (defaultManager == null) {
            defaultManager = new NetBeansLogManager();
        }
        return defaultManager;
    }
    
    public abstract void log(int severity, String message);
    
    public abstract void log(String message);
    
    public abstract boolean isDebugEnabled();
    
    public abstract void info(String message);

    public abstract void debug(String message);
    
    public abstract void debug(Throwable t);
    
    public abstract void error(String message);
    
    public abstract void error(Throwable t);

    public abstract void notify(int severity, Throwable t);
    
    public abstract void notify(Throwable t);
    
}


