package com.senseisoft.exeniumbot;

import static com.senseisoft.exeniumbot.Application.json;
import com.senseisoft.exeniumbot.telegram.AnswerCallbackRequest;
import com.senseisoft.exeniumbot.telegram.CallbackQuery;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.SendMessageResponse;
import com.senseisoft.exeniumbot.telegram.SendVideoRequest;
import com.senseisoft.exeniumbot.telegram.UpdateMessageRequest;
import com.senseisoft.exeniumbot.telegram.WebhookRequest;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;

public class TelegramAPI {

    public static void updateMessage(UpdateMessageRequest req) {
        post("editMessageText", req);
    }

    public static int sendMessage(SendMessageRequest req) {
        try {
            return ((SendMessageResponse) fetch("sendMessage", req, SendMessageResponse.class)).result.messageId;
        } catch (Exception e) {
            Log.exception(e);
            Log.err(new TelegramAPI(), "Couldn't send message to: " + req.chatId);
            return 0;
        }
    }

    public static int sendVideo(SendVideoRequest req) {
        try {
            return ((SendMessageResponse) fetch("sendVideo", req, SendMessageResponse.class)).result.messageId;
        } catch (Exception e) {
            Log.exception(e);
            Log.err(new TelegramAPI(), "Couldn't send video to: " + req.chatId);
            return 0;
        }
    }

    public static void setWebhook() {
        post("setWebhook", new WebhookRequest(
                Application.URL
                + (Application.URL.endsWith("/") ? "" : "/")
                + Application.TELEGRAM_API_TOKEN));
    }

    public static void answerCallback(CallbackQuery query) {
        post("answerCallbackQuery", new AnswerCallbackRequest(query.id));
    }

    private static void post(String method, Object data) {
        try {
            HttpPost post = new HttpPost("https://api.telegram.org/bot"
                    + Application.TELEGRAM_API_TOKEN + "/" + method);
            EntityBuilder eb = EntityBuilder.create();
            eb.setContentType(ContentType.create("application/json", "utf-8"));
            eb.setText(json.writeValueAsString(data));
            post.setEntity(eb.build());
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CloseableHttpResponse response = httpClient.execute(post);
            response.close();
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    private static <T> T fetch(String method, Object data, Class<T> type) {
        try {
            HttpPost post = new HttpPost("https://api.telegram.org/bot"
                    + Application.TELEGRAM_API_TOKEN + "/" + method);
            EntityBuilder eb = EntityBuilder.create();
            eb.setContentType(ContentType.create("application/json", "utf-8"));
            eb.setText(json.writeValueAsString(data));
            post.setEntity(eb.build());
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CloseableHttpResponse response = httpClient.execute(post);
            String body = Application.getStringContent(response);
            T result = Application.json.readValue(body, type);
            response.close();
            return result;
        } catch (Exception e) {
            Log.exception(e);
        }
        return null;
    }

}
