package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;


public class ExeniumCurrency implements Serializable {
    
    public int id;
    public String code;
    @JsonProperty("can_cashin")
    public boolean canCashin;
    
    public ExeniumCurrency() {        
    }

}
