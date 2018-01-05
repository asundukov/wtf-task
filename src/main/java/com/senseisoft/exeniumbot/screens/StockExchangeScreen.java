package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.Application;
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

public class StockExchangeScreen extends Screen {

    public StockExchangeScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {

        if (!Application.SCREEN_EXCHANGE) {
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_SCREEN_STOCK_EXCHANGE_NA"),
                    translations.get("BUTTON_BACK"));
        }

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(new InlineKeyboardButton("BTC/USD"), new InlineKeyboardButton("BTC/ETH")));
        rows.add(Arrays.asList(new InlineKeyboardButton("ETH/USD"), new InlineKeyboardButton("BTC/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("ETH/XNT"), new InlineKeyboardButton("USDT/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("ETC/XNT"), new InlineKeyboardButton("XEM/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("QTUM/XNT"), new InlineKeyboardButton("WAVES/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("CRM/XNT"), new InlineKeyboardButton("XNTB/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("DEEX/XNT"), new InlineKeyboardButton("TCO/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("PHI/XNT"), new InlineKeyboardButton("IRYO/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("CNV/XNT"), new InlineKeyboardButton("LTC/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton("BCH/XNT"), new InlineKeyboardButton("DASH/XNT")));
        rows.add(Arrays.asList(new InlineKeyboardButton(
                translations.get("BUTTON_ADD_CURRENCY_PAIR"), Application.ADD_PAIR_URL)));
        rows.add(Arrays.asList(new InlineKeyboardButton(translations.get("BUTTON_BACK"))));

        return new SendMessageRequest(user.id,
                translations.get("TEXT_SCREEN_STOCK_EXCHANGE"), new InlineKeyboardMarkup(rows, true));

    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (translations.get("BUTTON_BACK").equals(input)) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        } else {
            return new InputProcessingResult("StockExchangePairScreen", userData, true);
        }
    }

}
