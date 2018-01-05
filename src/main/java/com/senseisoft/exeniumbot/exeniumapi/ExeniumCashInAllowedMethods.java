package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class ExeniumCashInAllowedMethods {
    @JsonProperty("bank_wire")
    public Map<String, String> bankWire;
    public Map<String, String> crypt;

    public ExeniumCashInAllowedMethods() {
    }

}
