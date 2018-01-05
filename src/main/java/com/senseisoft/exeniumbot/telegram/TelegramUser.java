package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class TelegramUser implements Serializable {

    public int id;
    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;
    public String username;
    @JsonProperty("language_code")
    public String languageCode;

    public TelegramUser() {
    }
}
