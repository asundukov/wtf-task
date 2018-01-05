package com.senseisoft.exeniumbot.exeniumapi;

import java.io.Serializable;
import java.util.List;

public class ExeniumMarketPairs extends Pageable implements Serializable {

    public List<ExeniumMarketPair> items;
    
    public ExeniumMarketPairs() {
    }
}
