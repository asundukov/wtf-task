package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;

public class ExeniumWalletItem implements Serializable {
    public String id;
    @JsonProperty("currency_code")
    public String currencyCode;
    public BigDecimal balance;
    public ExeniumWalletItem() {
    }
}
