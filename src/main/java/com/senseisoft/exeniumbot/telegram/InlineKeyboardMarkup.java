package com.senseisoft.exeniumbot.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardMarkup implements Serializable {

    @JsonProperty("inline_keyboard")
    public List<List<InlineKeyboardButton>> inlineKeyboard;

    public InlineKeyboardMarkup() {
    }

    // one row
    public InlineKeyboardMarkup(String... buttons) {
        this.inlineKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String button : buttons) {
            row.add(new InlineKeyboardButton(button));
        }
        this.inlineKeyboard.add(row);
    }

    // multiple rows
    public InlineKeyboardMarkup(List<String>... buttonRows) {
        this.inlineKeyboard = new ArrayList<>();
        for (List<String> buttonRow : buttonRows) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (String button : buttonRow) {
                row.add(new InlineKeyboardButton(button));
            }
            this.inlineKeyboard.add(row);
        }
    }

    public InlineKeyboardMarkup(List<List<String>> buttonRows) {
        this.inlineKeyboard = new ArrayList<>();
        for (List<String> buttonRow : buttonRows) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (String button : buttonRow) {
                row.add(new InlineKeyboardButton(button));
            }
            this.inlineKeyboard.add(row);
        }
    }

    public InlineKeyboardMarkup(List<List<InlineKeyboardButton>> buttons, boolean fine) {
        this.inlineKeyboard = buttons;
    }

}
