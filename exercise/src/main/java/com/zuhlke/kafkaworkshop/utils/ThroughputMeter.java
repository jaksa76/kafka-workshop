package com.zuhlke.kafkaworkshop.utils;

public class ThroughputMeter {
    private long[] windows = new long[2];
    private int currentWindow = 0;
    private int previousWindow = 1;
    private long windowSize;
    private long startOfCurrentWindow;
    
    public ThroughputMeter(long windowSize) {
        this.windowSize = windowSize; 
        this.startOfCurrentWindow = 0;       
    }
    
    public void addUnits(long units) {
        switchWindowIfNeeded(System.currentTimeMillis());
        windows[currentWindow] += units;
    }
    
    public double getThroughput() {
        long now = System.currentTimeMillis();
        switchWindowIfNeeded(now);
        double previousWindowThroughput = 1000*windows[previousWindow]/windowSize;
        double currentWindowThroughput = 1000*windows[currentWindow]/(.0001 + now - startOfCurrentWindow);
        double ratio = (now - startOfCurrentWindow)/(double)windowSize;
        return (ratio*currentWindowThroughput + (1-ratio)*previousWindowThroughput);
    }
    
    private void switchWindowIfNeeded(long now) {        
        if (startOfCurrentWindow + windowSize < now) {
            previousWindow = currentWindow;
            currentWindow = (currentWindow + 1) % 2;
            windows[currentWindow] = 0;
            startOfCurrentWindow = now;
        }
    }
}
