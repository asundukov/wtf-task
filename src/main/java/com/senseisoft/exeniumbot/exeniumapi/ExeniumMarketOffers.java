package com.senseisoft.exeniumbot.exeniumapi;

import java.io.Serializable;
import java.util.List;

public class ExeniumMarketOffers extends Pageable implements Serializable {

    public List<ExeniumMarketOffer> items;
    
    public ExeniumMarketOffers() {
    }
}
