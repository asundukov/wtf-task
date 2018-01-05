package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;


public class ExeniumAuthRequest implements Serializable {
    
    @JsonProperty("external_service_code")
    public final String externalServiceCode = "telegram";
    @JsonProperty("user_external_id")
    public String userExternalId;
    
    public ExeniumAuthRequest() {        
    }
    
    public ExeniumAuthRequest(String userId) {        
        this.userExternalId = userId;
    }

}
