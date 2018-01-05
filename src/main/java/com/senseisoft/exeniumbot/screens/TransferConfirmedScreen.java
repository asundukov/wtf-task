package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransaction;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWalletItem;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.math.BigDecimal;

public class TransferConfirmedScreen extends Screen {

    public TransferConfirmedScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        ExeniumWalletItem wallet = new ExeniumWalletItem();
        for (ExeniumWalletItem w : userData.getWalletCache().items) {
            if (w.currencyCode.equals(userData.getInput("transfer currency"))) {
                wallet = w;
            }
        }
        ExeniumTransaction trans = exeniumAPI.transfer(
                userData.getExeniumId(),
                userData.getInput("transfer id"),
                wallet.id,
                new BigDecimal(userData.getInput("transfer amount")),
                userData.getInput("transfer comment"));

        if (trans == null) {
            return renderError();
        }

        return new SendMessageRequest(user.id,
                translations.get("TEXT_TRANSFER_COMPLETE"),
                translations.get("BUTTON_OK"));
    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_MAIN_MENU"))) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        return new InputProcessingResult("WalletScreen", userData, true);
    }

}
