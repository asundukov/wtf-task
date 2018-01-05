package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWallet;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWalletItem;
import com.senseisoft.exeniumbot.telegram.InlineKeyboardMarkup;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalletScreen extends Screen {

    public WalletScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        ExeniumWallet wallet = exeniumAPI.getWallet(userData.getExeniumId());
        if (wallet == null) {
            return renderError();
        }
        userData.setWalletCache(wallet);

        String text = translations.get("TEXT_YOUR_WALLET");
        for (ExeniumWalletItem item : wallet.items) {
            text += "\n" + item.currencyCode + ": " + translations.getFloat(item.balance);
        }

        List<List<String>> buttonRows = new ArrayList<>();
        buttonRows.add(Arrays.asList(
                translations.get("BUTTON_CASH_IN"),
                translations.get("BUTTON_CASH_OUT"),
                translations.get("BUTTON_HISTORY")));
        buttonRows.add(Arrays.asList(
                translations.get("BUTTON_TRANSFER"),
                translations.get("BUTTON_EXCHANGE")));
        buttonRows.add(Arrays.asList(
                translations.get("BUTTON_BACK")));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttonRows);

        return new SendMessageRequest(user.id, text, markup);
    }

    @Override
    public InputProcessingResult processInput(String input) {
        userData.flushInput();
        if (input.equals(translations.get("BUTTON_MAIN_MENU"))) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_CASH_IN"))) {
            return new InputProcessingResult("CashInScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_CASH_OUT"))) {
            return new InputProcessingResult("CashOutScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_HISTORY"))) {
            userData.setPage(1);
            return new InputProcessingResult("HistoryScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_TRANSFER"))) {
            return new InputProcessingResult("TransferScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_EXCHANGE"))) {
            return new InputProcessingResult("ExchangeScreen", userData, true);
        }
        return new InputProcessingResult("MainMenuScreen", userData, true);
    }
}
