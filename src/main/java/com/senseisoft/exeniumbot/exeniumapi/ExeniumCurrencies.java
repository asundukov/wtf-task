package com.senseisoft.exeniumbot.exeniumapi;

import java.io.Serializable;
import java.util.List;

public class ExeniumCurrencies extends Pageable implements Serializable {

    public List<ExeniumCurrency> items;

    public ExeniumCurrencies() {
    }
}
