package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.telegram.InlineKeyboardButton;
import com.senseisoft.exeniumbot.telegram.InlineKeyboardMarkup;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainMenuScreen extends Screen {

    public MainMenuScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                new InlineKeyboardButton(translations.get("BUTTON_WALLET")),
                new InlineKeyboardButton(translations.get("BUTTON_SUPPORT")),
                new InlineKeyboardButton(translations.get("BUTTON_STOCK_EXCHANGE"))));
        buttons.add(Arrays.asList(
                new InlineKeyboardButton(translations.get("BUTTON_EXENIUM_ICO")),
                new InlineKeyboardButton(
                        translations.get("BUTTON_EXENIUM_ICO_WEB"), cabinetURL())));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttons, true);

        String name = user.username;
        if (name == null) {
            if (user.firstName != null && user.lastName != null) {
                name = user.firstName + " " + user.lastName;
            } else {
                name = translations.get("TEXT_GENERIC_USERNAME");
            }
        }

        return new SendMessageRequest(user.id,
                translations.get("TEXT_WELCOME").replace("#username#", name)
                        .replace("#id#", userData.getExeniumId())
                        .replace("#cabinet_url#", cabinetURL()), markup);
    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_WALLET"))) {
            return new InputProcessingResult("WalletScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_SUPPORT"))) {
            return new InputProcessingResult("SupportScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_STOCK_EXCHANGE"))) {
            return new InputProcessingResult("StockExchangeScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_EXENIUM_ICO"))) {
            return new InputProcessingResult("ExeniumICOScreen", userData, true);
        }
        return new InputProcessingResult("MainMenuScreen", userData, true);
    }

}
