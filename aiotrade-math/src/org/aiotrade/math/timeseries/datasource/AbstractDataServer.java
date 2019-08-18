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
package org.aiotrade.math.timeseries.datasource;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.aiotrade.math.timeseries.Ser;
import org.aiotrade.math.timeseries.SerChangeEvent;

/**
 * This class will load the quote datas from data source to its data storage: quotes.
 * @TODO it will be implemented as a Data Server ?
 *
 * <K, V> data contract type, data pool type
 *
 * @author Caoyuan Deng
 */
public abstract class AbstractDataServer<K extends DataContract, V extends TimeValue> implements DataServer<K> {
    protected final static long ANCIENT_TIME = -1;
    private static Image DEFAULT_ICON;
    
    private static ExecutorService executorService;
    
    private Map<K, List<V>> contractMapStorage;
    private Map<K, Ser> subscribedContractMapSer;
    
    /** a quick seaching map */
    private Map<String, K> subscribedSymbolMapContract;
    
    /**
     * first ser is the master one,
     * second one (if available) is that who concerns first one.
     * Example: ticker ser also will compose today's quoteSer
     */
    private Map<Ser, Ser> serMapchainSer;
    
    private boolean inLoading;
    private LoadServer loadServer;
    
    private boolean inUpdating;
    private UpdateServer updateServer;
    private Thread updateThread;
    
    private int count;
    private DateFormat dateFormat;
    private InputStream inputStream;
    private long loadedTime;
    private long fromTime;
    
    public AbstractDataServer() {
    }
    
    /** this should be called before really usage */
    protected void init() {
        contractMapStorage = new HashMap<K, List<V>>();
        subscribedContractMapSer = new HashMap<K, Ser>();
        subscribedSymbolMapContract = new HashMap<String, K>();
        serMapchainSer = new HashMap<Ser, Ser>();
    }
    
    protected static ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(5);
        }
        
        return executorService;
    }
    
    protected final void setInputStream(InputStream is) {
        this.inputStream = is;
    }
    
    protected final InputStream getInputStream() {
        return inputStream;
    }
    
    protected final long getFromTime() {
        return fromTime;
    }
    
    protected final void setFromTime(long time) {
        this.fromTime = time;
    }
    
    protected final DateFormat getDateFormat() {
        if (dateFormat == null) {
            String dateFormatStr = getCurrentContract().getDateFormatString();
            if (dateFormat == null) {
                dateFormatStr = getDefaultDateFormatString();
            }
            dateFormat = new SimpleDateFormat(getCurrentContract().getDateFormatString(), Locale.US);
        }
        
        return dateFormat;
    }
    
    protected final void setLoadedTime(long time) {
        this.loadedTime = time;
    }
    
    protected final long getLoadedTime() {
        return loadedTime;
    }
    
    protected final void resetCount() {
        this.count = 0;
    }
    
    protected final void countOne() {
        this.count++;
        
        /*- @Reserve
         * Don't do refresh in loading any more, it may cause potential conflict
         * between connected refresh events (i.e. when processing one refresh event,
         * another event occured concurrent.)
         * if (count % 500 == 0 && System.currentTimeMillis() - startTime > 2000) {
         *     startTime = System.currentTimeMillis();
         *     preRefresh();
         *     fireDataUpdateEvent(new DataUpdatedEvent(this, DataUpdatedEvent.Type.RefreshInLoading, newestTime));
         *     System.out.println("refreshed: count " + count);
         * }
         */
    }
    
    protected final int getCount() {
        return count;
    }
    
    protected final List<V> getStorage(K contract) {
        List<V> storage = contractMapStorage.get(contract);
        if (storage == null) {
            storage = new ArrayList<V>();
            contractMapStorage.put(contract, storage);
        }
        
        return storage;
    }
    
    /**
     * @TODO
     * temporary method? As in some data feed, the symbol is not unique,
     * it may be same in different markets with different secType.
     */
    protected final K lookupContract(String symbol) {
        return subscribedSymbolMapContract.get(symbol);
    }
    
    private void releaseStorage(K contract) {
        /** don't get storage via getStorage(contract), which will create a new one if none */
        List<V> storage = contractMapStorage.get(contract);
        synchronized (contractMapStorage) {
            contractMapStorage.remove(contract);
        }
        if (storage != null) {
            returnBorrowedTimeValues(storage);
            synchronized (storage) {
                storage.clear();
            }
            storage = null;
        }
    }
    
    protected final boolean isAscending(List<V> storage) {
        final int size = storage.size();
        if (size <= 1) {
            return true;
        } else {
            for (int i = 0; i < size - 1; i++) {
                if (storage.get(i).getTime() < storage.get(i + 1).getTime()) {
                    return true;
                } else if (storage.get(i).getTime() > storage.get(i + 1).getTime()){
                    return false;
                } else {
                    continue;
                }
            }
        }
        
        return false;
    }
    
    protected abstract void returnBorrowedTimeValues(Collection<V> datas);
    
    protected K getCurrentContract() {
        /**
         * simplely return the contract currently in the front
         * @Todo, do we need to implement a scheduler in case of multiple contract?
         * Till now, only QuoteDataServer call this method, and they all use the
         * per server per contract approach.
         */
        for (K contract : getSubscribedContracts()) {
            return contract;
        }
        
        return null;
    }
    
    protected Collection<K> getSubscribedContracts() {
        return subscribedContractMapSer.keySet();
    }
    
    protected Ser getSer(K contract) {
        return subscribedContractMapSer.get(contract);
    }
    
    protected Ser getChainSer(Ser ser) {
        return serMapchainSer.get(ser);
    }
    
    /**
     * @param symbol symbol in source
     * @param set the Ser that will be filled by this server
     */
    public void subscribe(K contract, Ser ser) {
        subscribe(contract, ser, null);
    }
    
    public void subscribe(K contract, Ser ser, Ser chainSer) {
        synchronized (subscribedContractMapSer) {
            subscribedContractMapSer.put(contract, ser);
        }
        synchronized (subscribedSymbolMapContract) {
            subscribedSymbolMapContract.put(contract.getSymbol(), contract);
        }
        synchronized (serMapchainSer) {
            if (chainSer != null) {
                serMapchainSer.put(ser, chainSer);
            }
        }
    }
    
    public void unSubscribe(K contract) {
        cancelRequest(contract);
        synchronized (serMapchainSer) {
            serMapchainSer.remove(subscribedContractMapSer.get(contract));
        }
        synchronized (subscribedContractMapSer) {
            subscribedContractMapSer.remove(contract);
        }
        synchronized (subscribedSymbolMapContract) {
            subscribedSymbolMapContract.remove(contract.getSymbol());
        }
        releaseStorage(contract);
    }
    
    protected void cancelRequest(K contract) {}
    
    public boolean isContractSubsrcribed(K contract) {
        return subscribedContractMapSer.keySet().contains(contract);
    }
    
    public void startLoadServer() {
        if (getCurrentContract() == null) {
            assert false : "dataContract not set!";
        }
        
        if (subscribedContractMapSer.size() == 0) {
            assert false : ("none ser subscribed!");
        }
        
        if (loadServer == null) {
            loadServer = new LoadServer();
        }
        
        if (! inLoading) {
            inLoading = true;
            getExecutorService().submit(loadServer);
        }
    }
    
    public void startUpdateServer(int updateInterval) {
        if (inLoading) {
            System.out.println("should start update server after loaded");
            inUpdating = false;
            return;
        }
        
        inUpdating = true;
        if (updateServer == null) {
            updateServer = new UpdateServer(updateInterval);
        }
        if (updateThread == null) {
            updateThread = new Thread(updateServer);
        }
        if (! updateThread.isAlive()) {
            updateThread.setPriority(Thread.MIN_PRIORITY);
            updateThread.start();
        }
    }
    
    public void stopUpdateServer() {
        inUpdating = false;
        updateServer = null;
        updateThread = null;
        
        postStopUpdateServer();
    }
    
    protected void postStopUpdateServer() {}
    
    public boolean isInLoading() {
        return inLoading;
    }
    
    protected abstract void loadFromPersistence();
    /**
     * @param afterThisTime. when afterThisTime equals ANCIENT_TIME, you should
     *        process this condition.
     * @return loadedTime
     */
    protected abstract long loadFromSource(long afterThisTime);
    
    /**
     * compose ser using data from storage
     */
    public abstract SerChangeEvent composeSer(String symbol, Ser serToBeFilled, List<V> storage);
    
    
    protected class LoadServer implements Runnable {
        public void run() {
            loadFromPersistence();
            
            loadedTime = loadFromSource(loadedTime);
            
            inLoading = false;
            
            postLoad();
        }
    }
    
    protected void postLoad() {}
    
    public boolean isInUpdating() {
        return inUpdating;
    }
    
    private class UpdateServer implements Runnable {
        private int updateInterval = 10000;
        
        public UpdateServer(int updateInterval) {
            this.updateInterval = updateInterval;
        }
        
        public void run() {
            boolean inRoundUpdating = false;
            while (inUpdating && !inRoundUpdating) {
                try {
                    Thread.currentThread().sleep(updateInterval);
                } catch (InterruptedException e) {
                    inUpdating = false;
                    e.printStackTrace();
                }
                
                inRoundUpdating = true;
                
                long oldTime = loadedTime;
                
                loadedTime = loadFromSource(loadedTime);
                
                postUpdate();
                
                inRoundUpdating = false;
            }
        }
    }
    
    protected void postUpdate() {}
    
    public AbstractDataServer createNewInstance() {
        try {
            AbstractDataServer instance = (AbstractDataServer)getClass().newInstance();
            instance.init();
            
            return instance;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Override it to return your icon
     * @return a predifined image as the default icon
     */
    public Image getIcon() {
        if (DEFAULT_ICON == null) {
            URL url = AbstractDataServer.class.getResource("defaultIcon.gif");
            DEFAULT_ICON = url != null ? Toolkit.getDefaultToolkit().createImage(url) : null;
        }
        
        return DEFAULT_ICON;
    }
    
    /**
     * Convert source sn to source id in format of :
     * sn (0-63)       id (64 bits)
     * 0               ..,0000,0000
     * 1               ..,0000,0001
     * 2               ..,0000,0010
     * 3               ..,0000,0100
     * 4               ..,0000,1000
     * ...
     * @return source id
     */
    public final long getSourceId() {
        final byte sn = getSourceSerialNumber();
        assert sn >= 0 && sn < 63 : "source serial number should be between 0 to 63!";
        
        return sn == 0 ? 0 : 1 << (sn - 1);
    }
    
    public final int compareTo(DataServer another) {
        if (this.getDisplayName().equalsIgnoreCase(another.getDisplayName())) {
            return this.hashCode() < another.hashCode() ? -1 : (this.hashCode() == another.hashCode() ? 0 : 1);
        }
        return this.getDisplayName().compareTo(another.getDisplayName());
    }
    
    @Override
    public String toString() {
        return getDisplayName();
    }
}



