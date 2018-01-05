package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class SendVideoRequest implements Serializable {

    @JsonProperty("chat_id")
    public int chatId;
    public String video;

    public SendVideoRequest() {
    }

    public SendVideoRequest(int chatId, String video) {
        this.chatId = chatId;
        this.video = video;
    }
}
