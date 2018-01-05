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

public class StartScreen extends Screen {

    private final List<String> languageButtons;

    public StartScreen(Settings settings, TelegramUser user,
            UserData userData, ExeniumAPI exeniumAPI, List<String> languageButtons) {
        super(settings, null, user, userData, exeniumAPI);
        this.languageButtons = languageButtons;
    }

    public StartScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
        this.languageButtons = null;
    }

    @Override
    public SendMessageRequest render() {
        if (Application.WELCOME_VIDEO.startsWith("http")) {
            instantSendVideo(Application.WELCOME_VIDEO);
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String lang : languageButtons) {
            row.add(new InlineKeyboardButton(lang));
        }
        buttons.add(row);
        buttons.add(Arrays.asList(new InlineKeyboardButton("Web cabinet", cabinetURL())));

        return new SendMessageRequest(user.id,
                settings.getTextSelectLanguage().replace("#cabinet_url#", cabinetURL()),
                new InlineKeyboardMarkup(buttons, true));
    }

    @Override
    public InputProcessingResult processInput(String input) {
        userData.setLanguage(input);
        if (userData.isNewRegistration()) {
            return new InputProcessingResult("AgreementScreen", userData, true);
        }
        return new InputProcessingResult("MainMenuScreen", userData, true);
    }
}
