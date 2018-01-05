package com.senseisoft.exeniumbot.exeniumapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;


public class ExeniumUser implements Serializable {
    public String id;
    @JsonProperty("ref_id")
    public String refId;
    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;
    public String email;

    public ExeniumUser() {
    }
    
    public ExeniumUser(boolean test) {
        if (test) {
            this.id = "testid";
        }
    }
    
    public ExeniumUser(String id) {
        this.id = id;
    }
}
