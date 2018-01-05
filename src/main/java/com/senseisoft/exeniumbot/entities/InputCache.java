package com.senseisoft.exeniumbot.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// pseudo-entity stored as json
public class InputCache implements Serializable {

    public Map<String, String> cache;

    
    public void flush(String key) {
        cache.remove(key);
    }

    public InputCache() {
        this.cache = new HashMap<>();
    }
}
