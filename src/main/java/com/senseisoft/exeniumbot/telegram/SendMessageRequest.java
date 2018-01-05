package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendMessageRequest implements Serializable {

    @JsonProperty("chat_id")
    public int chatId;
    public String text;
    @JsonProperty("disable_web_page_preview")
    public boolean disableWebPagePreview = true;
    @JsonProperty("reply_markup")
    public InlineKeyboardMarkup replyMarkup;
    @JsonProperty("parse_mode")
    public final String parseMode = "HTML";

    public SendMessageRequest() {
    }

    public SendMessageRequest(int chatId, String text, String... buttons) {
        this.chatId = chatId;
        this.text = text;
        List<List<String>> markup = new ArrayList<>();
        markup.add(Arrays.asList(buttons));
        this.replyMarkup = new InlineKeyboardMarkup(markup);
    }

    public SendMessageRequest(int chatId, String text, List<String> buttons) {
        this.chatId = chatId;
        this.text = text;
        List<List<String>> markup = new ArrayList<>();
        markup.add(buttons);
        this.replyMarkup = new InlineKeyboardMarkup(markup);
    }

    public SendMessageRequest(int chatId, String text, InlineKeyboardMarkup markup) {
        this.chatId = chatId;
        this.text = text;
        this.replyMarkup = markup;
    }

    public SendMessageRequest(int chatId, String text) {
        this.chatId = chatId;
        this.text = text;
        this.replyMarkup = new InlineKeyboardMarkup(new ArrayList<>());
    }

    public SendMessageRequest(String chatId, String text) {
        try {
            this.chatId = Integer.parseInt(chatId);
        } catch (Exception e) {
            this.chatId = 0;
        }
        this.text = text;
        this.replyMarkup = new InlineKeyboardMarkup(new ArrayList<>());
    }
}
