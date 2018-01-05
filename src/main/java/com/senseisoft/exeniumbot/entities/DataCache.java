package com.senseisoft.exeniumbot.entities;

import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransactions;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWallet;
import java.io.Serializable;

// pseudo-entity stored as json
public class DataCache implements Serializable {
    public ExeniumWallet wallet;
    public ExeniumTransactions transactions;
    
    public DataCache() {
    }
}
