package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;

public class AgreementScreen extends Screen {

    public AgreementScreen(Settings settings, Translations translations, 
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {
        return new SendMessageRequest(user.id,
                translations.get("TEXT_AGREEMENT_MESSAGE"),
                translations.get("BUTTON_ACCEPT"));
    }

    @Override
    public InputProcessingResult processInput(String input) {
        if (input.equals(translations.get("BUTTON_ACCEPT"))) {
            userData.setNewRegistration(false);
            return new InputProcessingResult("MainMenuScreen", userData, true);
        }
        return new InputProcessingResult("AgreementScreen", userData, true);
    }

}
