package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class ExeniumMarketPair implements Serializable {

    @JsonProperty("currency_codes")
    public List<String> currencyCodes;
    
    public ExeniumMarketPair() {
    }
}
