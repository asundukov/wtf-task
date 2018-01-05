package com.senseisoft.exeniumbot.screens;

import com.senseisoft.exeniumbot.ExeniumAPI;
import com.senseisoft.exeniumbot.InputProcessingResult;
import com.senseisoft.exeniumbot.Screen;
import com.senseisoft.exeniumbot.Translations;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import com.senseisoft.exeniumbot.telegram.TelegramUser;

public class ExeniumICOReferralScreen extends Screen {

    public ExeniumICOReferralScreen(Settings settings, Translations translations,
            TelegramUser user, UserData userData, ExeniumAPI exeniumAPI) {
        super(settings, translations, user, userData, exeniumAPI);
    }

    @Override
    public SendMessageRequest render() {

        return new SendMessageRequest(user.id,
                translations.get("MASK_ICO_EXENIUM_REF_PROGRAM")
                        .replace("#id#", userData.getExeniumId()),
                translations.get("BUTTON_MAIN_MENU"));
    }

    @Override
    public InputProcessingResult processInput(String input) {
        return new InputProcessingResult("MainMenuScreen", userData, true);
    }

}
