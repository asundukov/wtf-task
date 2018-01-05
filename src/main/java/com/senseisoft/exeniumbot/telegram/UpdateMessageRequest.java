package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class UpdateMessageRequest implements Serializable {

    @JsonProperty("chat_id")
    public int chatId;
    @JsonProperty("message_id")
    public int messageId;
    public String text;
    @JsonProperty("disable_web_page_preview")
    public boolean disableWebPagePreview = true;
    @JsonProperty("reply_markup")
    public InlineKeyboardMarkup replyMarkup;
    @JsonProperty("parse_mode")
    public final String parseMode = "HTML";

    public UpdateMessageRequest() {
    }

    public UpdateMessageRequest(SendMessageRequest req, int messageId) {
        this.messageId = messageId;
        this.chatId = req.chatId;
        this.text = req.text;
        this.replyMarkup = req.replyMarkup;
    }
}
