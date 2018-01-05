package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class ExeniumMarketOffer implements Serializable {

    public int id;
    @JsonProperty("currency_code_buy")
    public String currencyCodeBuy;
    @JsonProperty("currency_code_sell")
    public String currencyCodeSell;
    public BigDecimal rate;
    public BigDecimal amount;
    
    public ExeniumMarketOffer() {
    }
}
