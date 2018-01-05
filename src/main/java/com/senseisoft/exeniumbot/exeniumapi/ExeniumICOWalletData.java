package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class ExeniumICOWalletData implements Serializable {

    @JsonProperty("wallet_id")
    public String walletId;

    public ExeniumICOWalletData() {
    }
}
