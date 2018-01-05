package com.senseisoft.exeniumbot;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;


public class PushRequest implements Serializable {
    
    @JsonProperty("api_key")
    public String apiKey;
    public List<String> recipients;
    public String message;
    @JsonProperty("recipients_type")
    public String recipientsType;
    
    public PushRequest() {        
    }

}
