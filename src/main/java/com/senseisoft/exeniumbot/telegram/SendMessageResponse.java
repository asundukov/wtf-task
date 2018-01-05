package com.senseisoft.exeniumbot.telegram;

import java.io.Serializable;

public class SendMessageResponse implements Serializable {

    public boolean ok;
    public Message result;

    public SendMessageResponse() {
    }
}
