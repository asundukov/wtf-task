package com.senseisoft.exeniumbot.exeniumapi;

import java.io.Serializable;
import java.util.List;

public class ExeniumWallet extends Pageable implements Serializable {

    public List<ExeniumWalletItem> items;

    public ExeniumWallet() {
    }
}
