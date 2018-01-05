package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.Application;
import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumICOStats;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWallet;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWalletItem;
import com.senseisoft.exeniumbot.telegram.InlineKeyboardMarkup;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExeniumICOScreen extends Screen {

    public ExeniumICOScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {

        if (!Application.SCREEN_ICO) {
            return new SendMessageRequest(user.id,
                    translations.get("TEXT_SCREEN_ICO_NA"),
                    translations.get("BUTTON_BACK"));
        }

        ExeniumICOStats stats = exeniumAPI.getICOStats();

        BigDecimal totalXNTB = stats == null ? null : stats.bountyXNTB
                .add(stats.welcomeXNTB)
                .add(stats.stageXNTB)
                .add(stats.referalXNTB);

        String soldXNT = stats == null || stats.soldXNT == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.soldXNT);
        String bountyXNTB = stats == null || stats.bountyXNTB == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.bountyXNTB);
        String stageXNTB = stats == null || stats.stageXNTB == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.stageXNTB);
        String welcomeXNTB = stats == null || stats.welcomeXNTB == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.welcomeXNTB);
        String teamXNT = stats == null || stats.teamXNT == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.teamXNT);
        String marketingXNT = stats == null || stats.marketingXNT == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.marketingXNT);
        String referalXNTB = stats == null || stats.referalXNTB == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.referalXNTB);
        String contributors = stats == null || stats.contributors == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.contributors);
        String totalEmitted = stats == null || stats.totalEmitted == null
                ? translations.get("TEXT_NO_DATA")
                : translations.getFloat(stats.totalEmitted);

        BigDecimal userXNT = new BigDecimal(0);
        BigDecimal userXNTB = new BigDecimal(0);
        ExeniumWallet wallet = exeniumAPI.getWallet(userData.getExeniumId());
        if (wallet != null) {
            for (ExeniumWalletItem item : wallet.items) {
                if ("XNT".equals(item.currencyCode) && item.balance != null) {
                    userXNT = item.balance;
                }
                if ("XNTB".equals(item.currencyCode) && item.balance != null) {
                    userXNTB = item.balance;
                }
            }
        }

        List<List<String>> buttonRows = new ArrayList<>();
        buttonRows.add(Arrays.asList(
                translations.get("BUTTON_ICO_BUY_TOKENS"),
                translations.get("BUTTON_ICO_REFERRAL_PROGRAM")));
        buttonRows.add(Arrays.asList(
                translations.get("BUTTON_BACK")));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttonRows);
        return new SendMessageRequest(user.id,
                translations.get("MASK_ICO_EXENIUM_INFO")
                        .replace("#sold_xnt#", soldXNT)
                        .replace("#bounty_xntb#", bountyXNTB)
                        .replace("#contributors#", contributors)
                        .replace("#total_emitted#", totalEmitted)
                        .replace("#stage_xntb#", stageXNTB)
                        .replace("#welcome_xntb#", welcomeXNTB)
                        .replace("#referal_xntb#", referalXNTB)
                        .replace("#team_xnt#", teamXNT)
                        .replace("#marketing_xnt#", marketingXNT)
                        .replace("#user_xnt#", translations.getFloat(userXNT))
                        .replace("#user_xntb#", translations.getFloat(userXNTB))
                        .replace("#user_total#", translations.getFloat(userXNT.add(userXNTB))),
                markup);
    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_MAIN_MENU"))) {
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_ICO_BUY_TOKENS"))) {
            userData.flushInput("exenium ico currency");
            return new InputProcessingResult("ExeniumICOBuyScreen", userData, true);
        }
        if (input.equals(translations.get("BUTTON_ICO_REFERRAL_PROGRAM"))) {
            return new InputProcessingResult("ExeniumICOReferralScreen", userData, true);
        }
        return new InputProcessingResult("MainMenuScreen", userData, true);
    }

}
