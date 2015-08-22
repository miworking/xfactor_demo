package org.twinone.locker.ui;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by abhishek on 8/2/2015.
 */
public class LRU extends LinkedHashMap<String,Integer> {
    private int capacity;

    public LRU(int capacity, float loadFactor){
        super(capacity, loadFactor, true);
        this.capacity = capacity;
    }

    /**
     * removeEldestEntry() should be overridden by the user, otherwise it will not
     * remove the oldest object from the Map.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest){
        return size() > this.capacity;
    }

}
