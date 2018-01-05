package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.Application;
import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumCashInMethods;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumCurrencies;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumCurrency;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.util.ArrayList;
import java.util.List;

public class CashInScreen extends Screen {

    public CashInScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        
        if (!Application.SCREEN_CASHIN) {
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_SCREEN_CASHIN_NA"),
                    translations.get("BUTTON_BACK"));
        }

        if (!userData.hasInput("cashin currency")) {
            List<String> currencies = new ArrayList<>();
            ExeniumCurrencies currs = exeniumAPI.getCurrencies(userData.getExeniumId());
            if (currs == null) {
                return renderError();
            }
            for (ExeniumCurrency currency : currs.items) {
                if (currency.canCashin) {
                    currencies.add(currency.code);
                }
            }

            return new SendMessageRequest(user.id,
                    translations.get("TEXT_CHOOSE_WALLET_FOR_CASHIN"),
                    formatCurrencies(currencies, translations.get("BUTTON_BACK")));
        }

        ExeniumCashInMethods methods = exeniumAPI.getCashInMethods(
                userData.getExeniumId(), userData.getInput("cashin currency"));

        String text = "";
        if (methods.allowed_methods.crypt != null) {
            text += translations.get("TEXT_CASH_IN_CRYPT")+"\n"
                    + methods.allowed_methods.crypt.get("address")+"\n";
        }

        if (methods.allowed_methods.bankWire != null) {
            text += translations.get("TEXT_CASH_IN_WIRE")
                    + methods.allowed_methods.bankWire.get("account_details");
        }

        return new SendMessageRequest(user.id, text.trim(),
                translations.get("BUTTON_BACK"));
    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_MAIN_MENU"))) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        
        if (input.equals(translations.get("BUTTON_BACK"))) {
            return new InputProcessingResult("WalletScreen", userData, true);
        }

        if (!userData.hasInput("cashin currency")) {
            userData.setInput("cashin currency", input);
            return new InputProcessingResult("CashInScreen", userData, true);
        }

        return new InputProcessingResult("WalletScreen", userData, true);
    }
}
