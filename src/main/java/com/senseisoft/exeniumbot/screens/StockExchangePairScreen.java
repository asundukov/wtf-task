package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;

public class StockExchangePairScreen extends Screen {

    public StockExchangePairScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {

        return new SendMessageRequest(user.id,
                translations.get("TEXT_SCREEN_STOCK_EXCHANGE_NA"),
                translations.get("BUTTON_BACK"));
    }

    @Override
    public InputProcessingResult processInput(String input) {
        return new InputProcessingResult("StockExchangeScreen", userData, true);
    }

}
