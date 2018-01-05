package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Message implements Serializable {

    @JsonProperty("message_id")
    public int messageId;
    public TelegramUser from;
    public int date;
    public String text;

    public Message() {
    }
}
