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

public class TransferScreen extends Screen {

    private UserData friendUserData;

    public TransferScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, UserData friendUserData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
        this.friendUserData = friendUserData;
    }

    public TransferScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {

        if (!Application.SCREEN_TRANSFER) {
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_SCREEN_TRANSFER_NA"),
                    translations.get("BUTTON_BACK"));
        }

        if (!userData.hasInput("transfer currency")) {
            return renderChooseWallet();
        }

        ExeniumWalletItem wallet = new ExeniumWalletItem();
        for (ExeniumWalletItem w : userData.getWalletCache().items) {
            if (w.currencyCode.equals(userData.getInput("transfer currency"))) {
                wallet = w;
            }
        }

        if (!userData.hasInput("transfer amount")) {
            return renderRequestAmount(wallet);
        }

        String check = checkAmount(userData.getInput("transfer amount"), wallet.balance);
        if (check.length() > 0) {
            userData.flushInput("transfer amount");
            return renderWrongAmount(check, wallet);
        }

        if (!userData.hasInput("transfer username")) {
            return renderRequestUsername();
        }

        if (!checkUsername(userData.getInput("transfer username"))) {
            userData.flushInput("transfer username");
            return renderWrongUsername();
        }

        if (friendUserData == null) {
            userData.flushInput("transfer username");
            return renderNoUser();
        }

        if (!userData.hasInput("transfer comment")) {
            return renderRequestComment();
        }

        return new SendMessageRequest(user.id,
                translations.get("MASK_APPROVE_TRANSFER")
                        .replace("#amount#", userData.getInput("transfer amount"))
                        .replace("#currency#", userData.getInput("transfer currency"))
                        .replace("#username#", userData.getInput("transfer username")),
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

        if (!userData.hasInput("transfer currency")) {
            userData.setInput("transfer currency", input);
            return new InputProcessingResult("TransferScreen", userData, true);
        }

        if (!userData.hasInput("transfer amount")) {
            userData.setInput("transfer amount", input);
            return new InputProcessingResult("TransferScreen", userData, false);
        }
        if (!userData.hasInput("transfer username")) {
            userData.setInput("transfer username", input.replace("@", ""));
            return new InputProcessingResult("TransferScreen", userData, false);
        }

        if (!userData.hasInput("transfer comment")) {
            userData.setInput("transfer comment", input);
            return new InputProcessingResult("TransferScreen", userData, false);
        }

        if (input.equals(translations.get("BUTTON_APPROVE"))) {
            userData.setInput("transfer id", friendUserData.getExeniumId());
            return new InputProcessingResult("TransferConfirmedScreen", userData, true);
        }

        return new InputProcessingResult("WalletScreen", userData, true);
    }

    private SendMessageRequest renderChooseWallet() {
        List<String> currencies = new ArrayList<>();
        ExeniumWallet wallet = userData.getWalletCache();
        for (ExeniumWalletItem item : wallet.items) {
            if (item.balance.signum() > 0) {
                currencies.add(item.currencyCode);
            }
        }

        if (currencies.isEmpty()) {
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_NO_MONEY_TO_TRANSFER"),
                    translations.get("BUTTON_MAIN_MENU"));
        }

        return new SendMessageRequest(user.id,
                translations.get("TEXT_CHOOSE_WALLET_FOR_TRANSFER"), currencies);
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

    private SendMessageRequest renderRequestUsername() {
        String text = translations.get("TEXT_TRANSFER_USERNAME");
        return new SendMessageRequest(user.id, text);
    }

    private SendMessageRequest renderWrongUsername() {
        String text = translations.get("TEXT_WRONG_USERNAME_FORMAT") + "\n"
                + translations.get("TEXT_TRANSFER_USERNAME");
        return new SendMessageRequest(user.id, text);
    }

    private SendMessageRequest renderNoUser() {
        String text = translations.get("TEXT_USERNAME_NOT_FOUND") + "\n"
                + translations.get("TEXT_TRANSFER_USERNAME");
        return new SendMessageRequest(user.id, text);
    }

    private SendMessageRequest renderRequestComment() {
        String text = translations.get("TEXT_TRANSFER_COMMENT");
        return new SendMessageRequest(user.id, text);
    }

    private String checkAmount(String amount, BigDecimal balance) {
        try {
            BigDecimal num = new BigDecimal(amount);
            if (num.signum() <= 0) {
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

    private boolean checkUsername(String username) {
        return true;
    }

}
