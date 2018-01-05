package com.senseisoft.exeniumbot.exeniumapi;

import java.io.Serializable;
import java.util.List;

public class ExeniumTransactions extends Pageable implements Serializable {

    public List<ExeniumTransaction> items;

    public ExeniumTransactions() {
    }
}
