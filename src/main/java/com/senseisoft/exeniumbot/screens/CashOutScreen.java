package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.Application;
import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWallet;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWalletItem;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashOutScreen extends Screen {

    public CashOutScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        
        if (!Application.SCREEN_CASHOUT) {
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_SCREEN_CASHOUT_NA"),
                    translations.get("BUTTON_BACK"));
        }

        if (!userData.hasInput("cashout currency")) {
            return renderChooseWallet();
        }

        ExeniumWalletItem wallet = new ExeniumWalletItem();
        for (ExeniumWalletItem w : userData.getWalletCache().items) {
            if (w.currencyCode.equals(userData.getInput("cashout currency"))) {
                wallet = w;
            }
        }

        if (!userData.hasInput("cashout amount")) {
            return renderRequestAmount(wallet);
        }

        String check = checkAmount(userData.getInput("cashout amount"), wallet.balance);
        if (check.length() > 0) {
            userData.flushInput("cashout amount");
            return renderWrongAmount(check, wallet);
        }

        if (!userData.hasInput("cashout address")) {
            return renderRequestAddress(wallet);
        }

        if (!checkAddress(userData.getInput("cashout address"))) {
            userData.flushInput("cashout address");
            return renderWrongAddress(wallet);
        }

        return new SendMessageRequest(user.id,
                translations.get("MASK_APPROVE_CASHOUT")
                        .replace("#amount#", userData.getInput("cashout amount"))
                        .replace("#currency#", userData.getInput("cashout currency"))
                        .replace("#address#", userData.getInput("cashout address")),
                translations.get("BUTTON_APPROVE"),
                translations.get("BUTTON_CANCEL")
        );

    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_MAIN_MENU"))) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        
        if (input.equals(translations.get("BUTTON_BACK"))) {
            return new InputProcessingResult("WalletScreen", userData, true);
        }
        
        if (!userData.hasInput("cashout currency")) {
            userData.setInput("cashout currency", input);
            return new InputProcessingResult("CashOutScreen", userData, true);
        }

        if (!userData.hasInput("cashout amount")) {
            userData.setInput("cashout amount", input);
            return new InputProcessingResult("CashOutScreen", userData, false);
        }
        if (!userData.hasInput("cashout address")) {
            userData.setInput("cashout address", input);
            return new InputProcessingResult("CashOutScreen", userData, false);
        }
        if (input.equals(translations.get("BUTTON_APPROVE"))) {
            return new InputProcessingResult("CashOutConfirmedScreen", userData, true);
        }

        return new InputProcessingResult("WalletScreen", userData, true);
    }

    private SendMessageRequest renderChooseWallet() {
        List<String> currencies = new ArrayList<>();
        ExeniumWallet wallet = userData.getWalletCache();
        if (wallet == null) {
            return renderError();
        }
        for (ExeniumWalletItem item : wallet.items) {
            if (item.balance.signum() > 0) {
                currencies.add(item.currencyCode);
            }
        }

        if (currencies.isEmpty()) {
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_NO_MONEY_TO_CASHOUT"),
                    translations.get("BUTTON_BACK"));
        }

        return new SendMessageRequest(user.id, translations.get("TEXT_CHOOSE_WALLET_FOR_CASHOUT"),
                formatCurrencies(currencies, translations.get("BUTTON_BACK")));
    }

    private SendMessageRequest renderRequestAmount(ExeniumWalletItem wallet) {
        String text = translations.get("MASK_CASHOUT_BALANCE")
                .replace("#currency#", wallet.currencyCode)
                .replace("#amount#", translations.getFloat(wallet.balance))
                + "\n" + translations.get("TEXT_REQUEST_AMOUNT");
        return new SendMessageRequest(user.id, text);
    }

    private SendMessageRequest renderWrongAmount(String check, ExeniumWalletItem wallet) {
        String text = check + "\n" + translations.get("MASK_CASHOUT_BALANCE")
                .replace("#currency#", wallet.currencyCode)
                .replace("#amount#", translations.getFloat(wallet.balance))
                + "\n" + translations.get("TEXT_REQUEST_AMOUNT");
        return new SendMessageRequest(user.id, text);
    }

    private SendMessageRequest renderRequestAddress(ExeniumWalletItem wallet) {
        String text = translations.get("MASK_CASHOUT_ADDRESS")
                .replace("#currency#", wallet.currencyCode);
        return new SendMessageRequest(user.id, text);
    }

    private SendMessageRequest renderWrongAddress(ExeniumWalletItem wallet) {
        String text = translations.get("TEXT_WRONG_ADDRESS_FORMAT") + "\n"
                + translations.get("MASK_CASHOUT_ADDRESS")
                        .replace("#currency#", wallet.currencyCode);
        return new SendMessageRequest(user.id, text);
    }

    private String checkAmount(String amount, BigDecimal balance) {
        try {
            BigDecimal num = new BigDecimal(amount);
            if (num.signum() < 1) {
                return translations.get("TEXT_AMOUNT_IS_LESS_OR_EQUALS_ZERO");
            }
            if (num.compareTo(balance) > 0) {
                return translations.get("TEXT_AMOUNT_IS_MORE_THAN_BALANCE");
            }
        } catch (Exception e) {
            return translations.get("TEXT_AMOUNT_IS_NOT_NUMBER");
        }
        return "";
    }

    private boolean checkAddress(String address) {
        return true;
    }
}
