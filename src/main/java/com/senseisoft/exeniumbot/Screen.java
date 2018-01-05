package com.senseisoft.exeniumbot;

import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.telegram.InlineKeyboardMarkup;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.SendVideoRequest;
import com.senseisoft.exeniumbot.telegram.UpdateMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;

public abstract class Screen {

    protected final Settings settings;
    protected final Translations translations;
    protected final TelegramUser user;
    protected final UserData userData;
    protected final ExeniumAPI exeniumAPI;

    public Screen(Settings settings, Translations translations, TelegramUser user,
            UserData userData, ExeniumAPI exeniumAPI) {
        this.settings = settings;
        this.user = user;
        this.translations = translations;
        this.userData = userData;
        this.exeniumAPI = exeniumAPI;
    }

    protected abstract SendMessageRequest render();

    protected abstract InputProcessingResult processInput(String input);

    public void update() {
        TelegramAPI.updateMessage(new UpdateMessageRequest(this.render(), userData.getMessageId()));
    }

    public void send() {
        userData.setMessageId(TelegramAPI.sendMessage(this.render()));
    }

    protected void instantUpdate(SendMessageRequest req) {
        TelegramAPI.updateMessage(new UpdateMessageRequest(req, userData.getMessageId()));
    }

    protected int instantSendVideo(String url) {
        return TelegramAPI.sendVideo(new SendVideoRequest(user.id, url));
    }

    protected SendMessageRequest renderError() {
        return new SendMessageRequest(user.id,
                translations.get("TEXT_API_ERROR"),
                translations.get("BUTTON_MAIN_MENU"));
    }

    protected List<List<String>> preformatCurrencies(Iterable<String> currencies) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        int i = 0;

        for (String currency : currencies) {
            if (++i % 5 == 0) {
                rows.add(row);
                row = new ArrayList<>();
            }
            row.add(currency);
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        return rows;
    }

    protected InlineKeyboardMarkup formatCurrencies(Iterable<String> currencies) {
        return new InlineKeyboardMarkup(preformatCurrencies(currencies));
    }

    protected InlineKeyboardMarkup formatCurrencies(Iterable<String> currencies, Iterable<String> last) {
        List<List<String>> rows = preformatCurrencies(currencies);
        List<String> row = new ArrayList<>();
        for (String el : last) {
            row.add(el);
        }
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    protected InlineKeyboardMarkup formatCurrencies(Iterable<String> currencies, String... last) {
        List<List<String>> rows = preformatCurrencies(currencies);
        List<String> row = new ArrayList<>();
        for (String el : last) {
            row.add(el);
        }
        rows.add(row);
        return new InlineKeyboardMarkup(rows);
    }

    protected String cabinetURL() {
        String pub = UUID.randomUUID().toString().replace("-", "");
        return Application.CABINET_URL + "?id=" + userData.getExeniumId()
                + "&key="
                + DigestUtils.md5Hex(Application.CABINET_SALT + userData.getExeniumId() + pub)
                + "-" + pub;
    }

}
