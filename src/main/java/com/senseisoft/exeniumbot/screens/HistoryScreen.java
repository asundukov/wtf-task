package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.Application;
import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransaction;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransactions;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.util.ArrayList;
import java.util.List;

public class HistoryScreen extends Screen {

    public HistoryScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        if (userData.hasInput("history transaction")) {
            return renderSingleTransaction();
        }

        ExeniumTransactions cache = userData.getTransactionsCache();
        ExeniumTransactions transactions = exeniumAPI.getTransactions(userData.getExeniumId(),
                userData.getPage(),
                cache == null || cache.items == null || cache.items.isEmpty()
                ? 0 : cache.total);
        userData.setTransactionsCache(transactions);

        if (transactions == null) {
            return renderError();
        }

        String text;
        if (transactions.items == null || transactions.items.isEmpty()) {
            text = translations.get("TEXT_NO_OPERATIONS");
        } else {
            text = translations.get("TEXT_LATEST_OPERATIONS") + "\n";
            for (ExeniumTransaction transaction : transactions.items) {
                text += "\n" + translations.get("MASK_OPERATIONS_LIST_ITEM")
                        .replaceAll("#br#", "\n")
                        .replace("#amount#", translations.getSignedFloat(transaction.amount))
                        .replace("#currency#", transaction.currencyCode)
                        .replace("#date#", translations.getDate(transaction.creationTime))
                        .replace("#time#", translations.getTime(transaction.creationTime))
                        .replace("#type#", translations.get("TEXT_OPERATION_" + transaction.type))
                        .replace("#link#", "/" + transaction.id);
            }
        }

        List<String> buttons = new ArrayList<>();
        if (userData.getPage() > 1) {
            buttons.add(translations.get("BUTTON_PREV"));
        }
        buttons.add(translations.get("BUTTON_BACK"));
        if (transactions != null
                && transactions.total > transactions.start + Application.PAGE_SIZE) {
            buttons.add(translations.get("BUTTON_NEXT"));
        }

        return new SendMessageRequest(user.id, text, buttons);
    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_MAIN_MENU"))) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_BACK"))) {
            if (userData.hasInput("history transaction")) {
                userData.flushInput();
                return new InputProcessingResult("HistoryScreen", userData, true);
            }
            return new InputProcessingResult("WalletScreen", userData, true);
        }

        if (input.equals(translations.get("BUTTON_NEXT"))) {
            userData.setPage(userData.getPage() + 1);
            return new InputProcessingResult("HistoryScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_PREV"))) {
            userData.setPage(userData.getPage() - 1);
            return new InputProcessingResult("HistoryScreen", userData, true);
        }

        if (!userData.hasInput("history transaction")) {
            userData.setInput("history transaction", input);
            return new InputProcessingResult("HistoryScreen", userData, true);
        }

        return new InputProcessingResult("WalletScreen", userData, true);
    }

    private SendMessageRequest renderSingleTransaction() {

        ExeniumTransactions cache = userData.getTransactionsCache();

        String text = "";

        for (ExeniumTransaction transaction : cache.items) {
            if (transaction.id.equals(userData.getInput("history transaction").substring(1))) {
                text = translations.get("MASK_OPERATIONS_DETAILED_ITEM")
                        .replace("#id#", transaction.id)
                        .replace("#currency#", transaction.currencyCode)
                        .replace("#amount#", translations.getSignedFloat(transaction.amount))
                        .replace("#date#", translations.getLongDate(transaction.creationTime))
                        .replace("#time#", translations.getLongTime(transaction.creationTime))
                        .replace("#type#", translations.get("TEXT_OPERATION_" + transaction.type))
                        .replace("#comment#", transaction.comment)
                        .replace("#id#", transaction.id);
            }
        }
        return new SendMessageRequest(user.id, text,
                translations.get("BUTTON_BACK"));
    }
}
