package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;


public class ExeniumDeeplinkRequest implements Serializable {
    @JsonProperty("user_id")
    public String userId;
    @JsonProperty("stat_id")
    public String statId;
    
    public ExeniumDeeplinkRequest() {        
    }
    
    public ExeniumDeeplinkRequest(String userId, String start) {        
        this.userId = userId;
        this.statId = start;
    }
}
