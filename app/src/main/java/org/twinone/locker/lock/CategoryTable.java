package org.twinone.locker.lock;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Skandy on 7/25/2015.
 */
public class CategoryTable implements Serializable{
    public Map<String, String> table = new HashMap<>();

    public CategoryTable(){}

    public void  put(String key, String value) {
        synchronized (this) {
            table.put(key, value);
        }
    }
}
