package com.senseisoft.exeniumbot.exeniumapi;

import java.io.Serializable;


public class ExeniumWalletRequest implements Serializable {
    public String id;
    
    public ExeniumWalletRequest() {        
    }
    
    public ExeniumWalletRequest(String userId) {        
        this.id = userId;
    }
}
