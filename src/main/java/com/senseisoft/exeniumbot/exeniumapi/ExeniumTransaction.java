package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;


public class ExeniumTransaction implements Serializable {
    public String id;
    public String type;
    public String comment;
    @JsonProperty("wallet_id")
    public String walletId;
    @JsonProperty("creation_time")
    public long creationTime;
    @JsonProperty("currency_code")
    public String currencyCode;
    public BigDecimal amount;
    public ExeniumTransaction() {
    }
}
