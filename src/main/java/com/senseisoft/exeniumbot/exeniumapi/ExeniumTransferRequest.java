package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class ExeniumTransferRequest implements Serializable {

    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("wallet_id")
    public String walletId;
    public BigDecimal amount;
    public String comment;
    @JsonProperty("reference_id")
    public String referenceId;
    
    public ExeniumTransferRequest() {
    }
    
    public ExeniumTransferRequest(String toUserId, String fromWalletId, 
            BigDecimal amount, String comment) {
        this.userId = toUserId;
        this.walletId = fromWalletId;
        this.amount = amount;
        this.comment = comment;
        this.referenceId = UUID.randomUUID().toString();
    }

}
