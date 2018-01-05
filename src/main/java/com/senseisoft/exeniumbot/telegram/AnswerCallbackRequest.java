package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class AnswerCallbackRequest implements Serializable {

    @JsonProperty("callback_query_id")
    public String callbackQueryId;

    public AnswerCallbackRequest() {
    }

    public AnswerCallbackRequest(String id) {
        this.callbackQueryId = id;
    }

}
