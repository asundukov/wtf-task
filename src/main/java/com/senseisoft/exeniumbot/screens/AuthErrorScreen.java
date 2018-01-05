package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;

public class AuthErrorScreen extends Screen {

    public AuthErrorScreen(Settings settings, Translations translations, 
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        return new SendMessageRequest(user.id, settings.getTextAuthError());
    }

    @Override
    public InputProcessingResult processInput(String input) {
        return new InputProcessingResult("StartScreen", userData, true);
    }

}
