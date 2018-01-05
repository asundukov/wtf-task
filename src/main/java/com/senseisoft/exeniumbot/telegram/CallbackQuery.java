package com.senseisoft.exeniumbot.telegram;

import java.io.Serializable;

public class CallbackQuery implements Serializable {

    public String id;
    public String data;
    public TelegramUser from;

    public CallbackQuery() {
    }
}
