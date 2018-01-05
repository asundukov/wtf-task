package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class ExeniumICOWallet implements Serializable {

    @JsonProperty("wallet_address")
    public String walletAddress;
    
    public ExeniumICOWallet() {
    }
}
