package com.senseisoft.exeniumbot.telegram;

import java.io.Serializable;

public class WebhookRequest implements Serializable {

    public String url;

    public WebhookRequest() {
    }

    public WebhookRequest(String url) {
        this.url = url;
    }

}
