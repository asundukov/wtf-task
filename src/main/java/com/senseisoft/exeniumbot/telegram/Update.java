package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Update implements Serializable {

    @JsonProperty("update_id")
    public int updateId;
    public Message message;
    @JsonProperty("callback_query")
    public CallbackQuery callbackQuery;

    public Update() {
    }
}
