package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InlineKeyboardButton implements Serializable {

    public String text;
    public String url;
    @JsonProperty("callback_data")
    public String callbackData;

    public InlineKeyboardButton() {
    }

    public InlineKeyboardButton(String text) {
        this.text = text;
        this.callbackData = text;
    }

    public InlineKeyboardButton(String text, String url) {
        this.text = text;
        this.url = url;
    }
}
