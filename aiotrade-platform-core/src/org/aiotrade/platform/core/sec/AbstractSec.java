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
import java.util.HashMap;
import java.util.Map;
import org.aiotrade.math.timeseries.Frequency;
import org.aiotrade.math.timeseries.Ser;
import org.aiotrade.math.timeseries.datasource.DataContract;
import org.aiotrade.math.timeseries.SerChangeEvent;
import org.aiotrade.math.timeseries.SerChangeListener;
import org.aiotrade.platform.core.dataserver.QuoteServer;
import org.aiotrade.platform.core.dataserver.TickerServer;
import org.aiotrade.math.timeseries.QuoteSer;
import org.aiotrade.platform.core.dataserver.QuoteContract;
import org.aiotrade.platform.core.dataserver.TickerContract;

/**
 * An implement of Sec.
 * each sofic has a default quoteSer and a tickerSer which will be created in the
 * initialization. The quoteSer will be put in the freq-ser map, the tickerSer
 * won't be.
 * You may put ser from outside, to the freq-ser map, so each sofic may have multiple
 * freq sers, but only per freq pre ser is allowed.
 *
 *
 * @author Caoyuan Deng
 */
public abstract class AbstractSec implements Sec {
    
    private String uniSymbol; // a globe uniSymbol, may have different source uniSymbol.
    private String description;
    private String name;
    private QuoteContract quoteContract;
    
    private Frequency defaultFreq;
    
    /** each freq may have a standalone quoteDataServer for easy control and thread safe */
    private Map<Frequency, QuoteServer> freqMapQuoteServer;
    private Map<Frequency, QuoteSer> freqMapSer = new HashMap<Frequency, QuoteSer>();
    
    /** tickerContract will always be built according to quoteContrat ? if so, set it final */
    private final TickerContract tickerContract;
    private QuoteSer tickerSer;
    private TickerServer tickerServer;
    
    /**
     * As it's a SerProvider, when new create it, we also assign a quoteContract
     * to it, which is a the one with the default freq. And we can use it to
     * subscribe data server for various freqs, since the data server will invoke
     * the proper freq server according to ser's freq (the ser should be given to
     * the server meanwhile when you subscribe.
     *
     * The default contract holds most of the information to be used.
     */
    public AbstractSec(String uniSymbol, QuoteContract quoteContract) {
        this.uniSymbol = uniSymbol;
        this.name = uniSymbol.replace('.', '_');
        this.quoteContract = quoteContract;
        this.defaultFreq = quoteContract.getFreq();
        
        /** create default freq ser */
        freqMapSer.put(defaultFreq, new QuoteSer(defaultFreq));
        
        /** create tickerSer */
        this.tickerContract = new TickerContract();
        tickerContract.setSymbol(quoteContract.getSymbol());
        tickerSer = new QuoteSer(tickerContract.getFreq());
    }
    
    public QuoteSer getSer(Frequency freq) {
        return freqMapSer.get(freq);
    }
    
    public void putSer(QuoteSer ser) {
        freqMapSer.put(ser.getFreq(), ser);
    }
    
    /**
     * synchronized this method to avoid conflict on variable: loadBeginning and
     * concurrent accessing to those maps.
     */
    public synchronized boolean loadSer(Frequency freq) {
        
        /** ask contract instead of server */
        if (!quoteContract.isFreqSupported(freq)) {
            return false;
        }
        
        if (freqMapQuoteServer == null) {
            freqMapQuoteServer = new HashMap<Frequency, QuoteServer>();
        }
        
        QuoteServer quoteServer = freqMapQuoteServer.get(freq);
        if (quoteServer == null) {
            quoteServer = quoteContract.getServiceInstance();
            freqMapQuoteServer.put(freq, quoteServer);
        }
        
        QuoteSer serToBeLoaded = getSer(freq);
        if (serToBeLoaded == null) {
            serToBeLoaded = new QuoteSer(freq);
            freqMapSer.put(freq, serToBeLoaded);
        }
        
        if (!quoteServer.isContractSubsrcribed(quoteContract)) {
            quoteServer.subscribe(quoteContract, serToBeLoaded);
        }
        
        boolean loadBeginning = false;
        /** If there is already a dataServer running and not finished, don't load again */
        if (quoteServer.isInLoading()) {
            System.out.println("A loading procedure is already running and not finished yet!");
            loadBeginning = false;
            serToBeLoaded.setLoaded(true);
        } else {
            quoteServer.startLoadServer();
            loadBeginning = true;
            serToBeLoaded.setLoaded(true);
        }
        
        if (loadBeginning) {
            SerChangeListener listener = new SerChangeListener() {
                public void serChanged(SerChangeEvent evt) {
                    Ser sourceSer = evt.getSource();
                    QuoteServer quoteServer = freqMapQuoteServer.get(sourceSer.getFreq());
                    switch (evt.getType()) {
                        case FinishedLoading:
                            if (quoteServer != null) {
                                if (quoteContract.isRefreshable()) {
                                    quoteServer.startUpdateServer(quoteContract.getRefereshInterval() * 1000);
                                } else {
                                    quoteServer.unSubscribe(quoteContract);
                                    freqMapQuoteServer.remove(sourceSer.getFreq());
                                }
                            }
                            sourceSer.removeSerChangeListener(this);
                            break;
                        default:
                    }
                }
            };
            serToBeLoaded.addSerChangeListener(listener);
        }
        
        return loadBeginning;
    }
    
    public boolean isSerLoaded(Frequency freq) {
        Ser ser = freqMapSer.get(freq);
        return ser != null ? ser.isLoaded() : false;
    }
    
    public QuoteSer getTickerSer() {
        return tickerSer;
    }
    
    public void setUniSymbol(String symbol) {
        this.uniSymbol = symbol;
        name = symbol.replace('.', '_');
    }
    
    public String getUniSymbol() {
        return uniSymbol;
    }
    
    public String getName() {
        return name;
    }
    
    public void stopAllDataServer() {
        if (freqMapQuoteServer != null) {
            for (QuoteServer server : freqMapQuoteServer.values()) {
                if (server.isInUpdating()) {
                    server.stopUpdateServer();
                }
            }
            freqMapQuoteServer.clear();
        }
    }
    
    public void clearSer(Frequency freq) {
        Ser ser = getSer(freq);
        ser.clear(0);
        ser.setLoaded(false);
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String toString() {
        return uniSymbol;
    }
    
    public void setDataContract(DataContract quoteSourceDescriptor) {
        this.quoteContract = (QuoteContract)quoteSourceDescriptor;
        /** may need a new dataServer now: */
        freqMapQuoteServer.clear();
    }
    
    public DataContract getDataContract() {
        return quoteContract;
    }
    
    public TickerServer getTickerServer() {
        return tickerServer;
    }
    
    public TickerContract getTickerContract() {
        return tickerContract;
    }
    
    public void subscribeTickerServer() {
        assert tickerContract != null : "ticker contract not set yet !";
        
        /**
         * @TODO, temporary test code
         */
        tickerContract.setSymbol(quoteContract.getSymbol());
        if (quoteContract.getServiceClassName().toUpperCase().contains("IB")) {
            tickerContract.setServiceClassName("org.aiotrade.platform.modules.dataserver.ib.IBTickerServer");
        } else {
            tickerContract.setServiceClassName("org.aiotrade.platform.modules.dataserver.basic.YahooTickerServer");
        }
        
        startTickerServerIfNecessary();
    }
    
    private void startTickerServerIfNecessary() {
        /**
         * @TODO, if tickerServer switched, should check here.
         */
        if (tickerServer == null) {
            tickerServer = tickerContract.getServiceInstance();
        }
        
        if (!tickerServer.isContractSubsrcribed(tickerContract)) {
            tickerServer.subscribe(tickerContract, getTickerSer(), getSer(defaultFreq));
        }
        
        if (!tickerServer.isInUpdating()) {
            tickerServer.startUpdateServer(tickerContract.getRefereshInterval() * 1000);
        }
    }
    
    public void unSubscribeTickerServer() {
        if (tickerServer != null && tickerContract != null) {
            tickerServer.unSubscribe(tickerContract);
        }
    }
    
    public boolean isTickerServerSubscribed() {
        return tickerServer != null ?
            tickerServer.isContractSubsrcribed(tickerContract) : false;
    }
    
}

