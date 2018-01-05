package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.Application;
import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumICOWallet;
import com.senseisoft.exeniumbot.telegram.InlineKeyboardButton;
import com.senseisoft.exeniumbot.telegram.InlineKeyboardMarkup;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExeniumICOBuyScreen extends Screen {

    public ExeniumICOBuyScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {

        List<InlineKeyboardButton> currencies = new ArrayList<>();
        if (Application.ICO_ETH) {
            currencies.add(new InlineKeyboardButton("ETH"));
        }
        if (Application.ICO_BTC) {
            currencies.add(new InlineKeyboardButton("BTC"));
        }
        if (Application.ICO_BCH) {
            currencies.add(new InlineKeyboardButton("BCH"));
        }
        if (Application.ICO_ETC) {
            currencies.add(new InlineKeyboardButton("ETC"));
        }

        if (!userData.hasInput("exenium ico currency")
                || currencies.stream().noneMatch(s -> s.text.equals(userData.getInput("exenium ico currency")))) {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            rows.add(currencies);
            rows.add(Arrays.asList(new InlineKeyboardButton(
                    translations.get("TEXT_ICO_OTHER_CURRENCIES"), cabinetURL())));
            rows.add(Arrays.asList(new InlineKeyboardButton(translations.get("BUTTON_MAIN_MENU"))));
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_ICO_EXENIUM_BUY_TOKENS"), new InlineKeyboardMarkup(rows, true));
        }

        instantUpdate(new SendMessageRequest(user.id, translations.get("TEXT_WAIT_FOR_WALLET")));
        ExeniumICOWallet wallet = exeniumAPI.getExeniumICOWallet(
                userData, userData.getInput("exenium ico currency"));

        if (wallet == null) {
            return renderError();
        }

        return new SendMessageRequest(user.id,
                translations.get("MASK_ICO_EXENIUM_DEPO_ADDRESS")
                        .replace("#address#", wallet.walletAddress),
                translations.get("BUTTON_MAIN_MENU"));
    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_MAIN_MENU"))) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        userData.setInput("exenium ico currency", input);

        return new InputProcessingResult("ExeniumICOBuyScreen", userData, true);
    }

}
