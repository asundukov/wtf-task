package com.senseisoft.exeniumbot;

import com.senseisoft.exeniumbot.entities.Language;
import com.senseisoft.exeniumbot.entities.Settings;
import com.senseisoft.exeniumbot.entities.Stat;
import com.senseisoft.exeniumbot.entities.Translation;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumUser;
import com.senseisoft.exeniumbot.repositories.LanguageRepository;
import com.senseisoft.exeniumbot.repositories.SettingsRepository;
import com.senseisoft.exeniumbot.repositories.StatRepository;
import com.senseisoft.exeniumbot.repositories.TranslationRepository;
import com.senseisoft.exeniumbot.screens.StartScreen;
import com.senseisoft.exeniumbot.telegram.Update;
import com.senseisoft.exeniumbot.telegram.TelegramUser;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.senseisoft.exeniumbot.repositories.UserDataRepository;
import com.senseisoft.exeniumbot.screens.AuthErrorScreen;
import com.senseisoft.exeniumbot.screens.DeletedScreen;
import com.senseisoft.exeniumbot.screens.TransferScreen;
import com.senseisoft.exeniumbot.telegram.SendMessageRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

@Service
public class BotController {

    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private TranslationRepository translationRepository;
    @Autowired
    private UserDataRepository userDataRepository;
    @Autowired
    private StatRepository statRepository;

    @Autowired
    private ExeniumAPI exeniumAPI;

    @Value("${bot.apikey}")
    private String apiKey;

    public void push(PushRequest request) {
        if (request.apiKey == null
                || request.message == null || !request.apiKey.equals(apiKey)) {
            return;
        }

        if (request.recipients == null && request.recipientsType == null) {
            Iterable<UserData> users = userDataRepository.findAll();
            for (UserData user : users) {
                if (user.getTelegramId() != null) {
                    TelegramAPI.sendMessage(new SendMessageRequest(
                            user.getTelegramId(), request.message));
                }
            }

        }

        for (String id : request.recipients) {
            UserData user = null;

            if ("telegram_username".equals(request.recipientsType)) {
                user = userDataRepository.findByTelegramUsername(id);
            } else if ("exenium_id".equals(request.recipientsType)) {
                user = userDataRepository.findByExeniumId(id);
            }

            if (user == null) {
                continue;
            }

            TelegramAPI.sendMessage(new SendMessageRequest(
                    user.getTelegramId(), request.message));
        }

    }

    public void handle(Update update)
            throws ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {

        if (update == null || (update.message == null && update.callbackQuery == null)) {
            return;
        }

        TelegramUser user = update.callbackQuery == null || update.callbackQuery.from == null
                ? update.message.from : update.callbackQuery.from;
        if (user == null) {
            Log.err(this, "Null user from Telegram, aborting.");
            return;
        }
        String text = update.callbackQuery == null || update.callbackQuery.data == null
                ? update.message.text : update.callbackQuery.data;

        UserData userData = getUserData("" + user.id, user.username);

        if (userData == null) {
            new AuthErrorScreen(getSettings(), null, user, null, null).send();
            return;
        }

        String currentScreenName = userData.getCurrentScreen();
        if (currentScreenName == null || text.startsWith("/start")) {
            StartScreen screen
                    = new StartScreen(getSettings(),
                            user, userData, exeniumAPI, getLanguageButtons());

            screen.send();
            setCurrentScreen(userData, "StartScreen");
            String payload = "";
            if (text.startsWith("/start ")) {
                payload = text.replace("/start ", "");
                Optional<Stat> stat = statRepository.findByUserDataAndStatId(userData, payload);
                if (!stat.isPresent()) {
                    statRepository.save(new Stat(payload, userData));
                }
            }
            exeniumAPI.cpa(userData.getExeniumId(), payload);

            return;
        }

        Settings settings = getSettings();
        Optional<Language> language = languageRepository.findByCode(userData.getLanguage());

        if ("StartScreen".equals(currentScreenName)) {
            Screen currentScreen = new StartScreen(getSettings(),
                    user, userData, exeniumAPI, getLanguageButtons());
            InputProcessingResult result = currentScreen.processInput(text);
            language = languageRepository.findByCode(result.userData.getLanguage());
            if (!language.isPresent()) {
                currentScreen.send();
                setCurrentScreen(userData, "StartScreen");
                return;
            }
        }

        Translations translations = getTranslations(language.get());

        Screen currentScreen = reflect(currentScreenName, user, userData, settings, translations);
        InputProcessingResult result = currentScreen.processInput(text);

        if (result.update) {
            if (update.callbackQuery == null || update.callbackQuery.data == null) {
                new DeletedScreen(settings, translations, user, userData, exeniumAPI).update();
                reflect(result.nextScreen, user, userData, settings, translations).send();
            } else {
                reflect(result.nextScreen, user, userData, settings, translations).update();
            }
        } else {
            reflect(result.nextScreen, user, userData, settings, translations).send();

        }
        userData = result.userData;
        setCurrentScreen(userData, result.nextScreen);
        if (update.callbackQuery != null) {
            TelegramAPI.answerCallback(update.callbackQuery);
        }
    }

    private Screen reflect(String name, TelegramUser user, UserData userData,
            Settings settings, Translations translations)
            throws ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {

        if ("TransferScreen".equals(name)
                && userData.hasInput("transfer username")) {
            return new TransferScreen(
                    settings, translations, user, userData,
                    userDataRepository.findByTelegramUsername(userData.getInput("transfer username")), exeniumAPI);
        }

        Class<?> cls = Class.forName("com.senseisoft.exeniumbot.screens." + name);
        Constructor<?> constructor
                = cls.getConstructor(Settings.class, Translations.class,
                        TelegramUser.class, UserData.class, ExeniumAPI.class);

        return (Screen) constructor.newInstance(settings,
                translations, user, userData, exeniumAPI);
    }

    private Settings getSettings() {

        prepopTranslations();

        if (settingsRepository.count() == 0) {
            Log.info(this, "No settings found in the database; populating with test data.");
            settingsRepository.save(new Settings());
        }

        for (Settings res : settingsRepository.findAll()) {
            // only one row matters
            return res;
        }

        Log.err(this, "Settings were lost during runtime!");
        return new Settings();
    }

    private UserData getUserData(String telegramId, String telegramUsername) {
        Optional<UserData> data = userDataRepository.findByTelegramId(telegramId);
        return data.isPresent()
                ? exeniumAuth(data.get())
                : exeniumAuth(new UserData(telegramId, telegramUsername, "RUS"));
    }

    private UserData exeniumAuth(UserData data) {
        if (data.getExeniumId() != null) {
            return data;
        }
        ExeniumUser user = exeniumAPI.auth(data.getTelegramId());
        if (user == null) {
            return null;
        }
        data.setExeniumId(user.id);
        data.setRefId(user.refId != null ? user.refId : "");
        return data;
    }

    private Translations getTranslations(Language lang) {
        Map<String, String> result = new HashMap<>();
        List<Translation> translations = translationRepository.findByLanguage(lang);
        for (Translation translation : translations) {
            if (!result.containsKey(translation.getTranslationKey())) {
                result.put(translation.getTranslationKey(), translation.getTranslationValue());
            } else {
                Log.warn(this, "Duplicate translation key: "
                        + translation.getTranslationKey() + " for " + lang.getName());
            }
        }
        return new Translations(lang, result);
    }

    private List<String> getLanguageButtons() {
        List<String> res = new ArrayList<>();
        for (Language lang : languageRepository.findAll()) {
            res.add(lang.getCode());
        }
        return res;
    }

    private void setCurrentScreen(UserData userData, String screen) {
        userData.setCurrentScreen(screen);
        userDataRepository.save(userData);
    }

    private void prepopTranslations() {
        Optional<Language> rus = languageRepository.findByCode("RUS");
        if (!rus.isPresent()) {
            Log.info(this, "No languages found in the database; populating with test data.");
            Language lang = new Language("Russian", "RUS", "ru-RU", "");
            lang = languageRepository.save(lang);

            translationRepository.save(new Translation(lang,
                    "TEXT_DELETED", "\uD83D\uDCA4"));

            translationRepository.save(new Translation(lang, "TEXT_WELCOME",
                    "Приветствую, #username#! Это быстрая,надежная криптовалютная биржа "
                    + "с возможностью обмена и перевода криптовалют, "
                    + "фиатных чисел между пользователями.\n"
                    + "\n"
                    + "Разработка компании LORIDANO HOLDING LTD "
                    + "Certificate of incorporation № HE 373218 "
                    + "Registered office: Arch. Makariou III, 155, "
                    + "PROTEAS HOUSE, 5th floor, 3026 Limassol\n\n"
                    + "id: #id#\n"
                    + "#cabinet_url#"));

            translationRepository.save(new Translation(lang,
                    "TEXT_SUPPORT_MESSAGE", "* "
                    + "<a href=\"https://exenium.io\">https://exenium.io</a>\n"
                    + "\n"
                    + "* Новости - @exenium\n"
                    + "\n"
                    + "* Поддержка - @zevakinm\n"
                    + "\n"
                    + "* Инструкции и комиссии - "
                    + "<a href=\"https://goo.gl/6fm61K\">https://goo.gl/6fm61K</a>\n"
                    + "\n"
                    + "* Политика борьбы с отмыванием денег - "
                    + "<a href=\"https://goo.gl/36zWSy\">https://goo.gl/36zWSy</a>\n"
                    + "\n"
                    + "* https://goo.gl/ih39bq"));

            translationRepository.save(new Translation(lang,
                    "TEXT_YOUR_WALLET", "Ваш кошелек:"));
            translationRepository.save(new Translation(lang,
                    "TEXT_CHOOSE_WALLET_FOR_CASHIN", "Выберите валюту пополнения"));
            translationRepository.save(new Translation(lang,
                    "TEXT_CHOOSE_WALLET_FOR_CASHOUT", "Выберите валюту вывода"));
            translationRepository.save(new Translation(lang,
                    "TEXT_NO_OPERATIONS", "Операции отсутствуют"));
            translationRepository.save(new Translation(lang,
                    "TEXT_LATEST_OPERATIONS", "Последние операции:"));
            translationRepository.save(new Translation(lang,
                    "TEXT_OPERATION_REPLENISHMENT", "пополнение"));
            translationRepository.save(new Translation(lang,
                    "TEXT_CASH_IN_CRYPT", "Адрес кошелька"));
            translationRepository.save(new Translation(lang,
                    "TEXT_CASH_IN_WIRE", "Банковские реквизиты"));
            translationRepository.save(new Translation(lang,
                    "TEXT_NO_MONEY_TO_CASHOUT", "Отсутствуют доступные для вывода средства"));
            translationRepository.save(new Translation(lang,
                    "TEXT_CHOOSE_WALLET_FOR_TRANSFER", "Выберите валюту пeревода"));
            translationRepository.save(new Translation(lang,
                    "TEXT_REQUEST_AMOUNT", "Введите сумму"));
            translationRepository.save(new Translation(lang,
                    "TEXT_TRANSFER_USERNAME", "Введите никнейм "
                    + "пользователя Telegram, которому хотите перевести средства."));
            translationRepository.save(new Translation(lang,
                    "TEXT_AMOUNT_IS_MORE_THAN_BALANCE", "Сумма не должна быть больше чем баланс"));
            translationRepository.save(new Translation(lang,
                    "TEXT_USERNAME_NOT_FOUND", "Пользователь не найден "
                    + "или отключил возможность перевода через никнейм."));
            translationRepository.save(new Translation(lang,
                    "TEXT_TRANSFER_COMPLETE", "Средства будут перечислены в ближайшее время."));
            translationRepository.save(new Translation(lang,
                    "TEXT_CASHOUT_COMPLETE", "Средства будут перечислены в ближайшее время."));
            translationRepository.save(new Translation(lang,
                    "TEXT_ICO_EXENIUM_BUY_TOKENS", "В настоящий момент токены продаются за следующие валюты:"));
            translationRepository.save(new Translation(lang,
                    "TEXT_CHOOSE_OFFER_CURRENCY", "Выберите тикер"));
            translationRepository.save(new Translation(lang,
                    "TEXT_SCREEN_ICO_NA", "ICO недоступно до 14.02"));
            translationRepository.save(new Translation(lang,
                    "TEXT_SCREEN_STOCK_EXCHANGE_NA", "Биржа в разработке"));
            translationRepository.save(new Translation(lang,
                    "TEXT_SCREEN_TRANSFER_NA", "Переводы в разработке"));
            translationRepository.save(new Translation(lang,
                    "TEXT_SCREEN_EXCHANGE_NA", "Обмен валют в разработке"));
            translationRepository.save(new Translation(lang,
                    "TEXT_SCREEN_CASHIN_NA", "Пополнение в разработке"));
            translationRepository.save(new Translation(lang,
                    "TEXT_SCREEN_CASHOUT_NA", "Вывод в разработке"));
            translationRepository.save(new Translation(lang,
                    "TEXT_API_ERROR", "Ошибка соединения с сервисом"));
            translationRepository.save(new Translation(lang,
                    "TEXT_WAIT_FOR_WALLET", "Генерация и проверка кошелька..."));
            translationRepository.save(new Translation(lang,
                    "TEXT_NO_DATA", "нет данных"));
            translationRepository.save(new Translation(lang,
                    "TEXT_GENERIC_USERNAME", "уважаемый пользователь"));

            translationRepository.save(new Translation(lang,
                    "MASK_OPERATIONS_LIST_ITEM",
                    "#date# #time# #type# #br# #amount# #currency# #link#"));
            translationRepository.save(new Translation(lang,
                    "MASK_OPERATIONS_DETAILED_ITEM",
                    "Операция #id#\n"
                    + "Валюта: #currency#\n"
                    + "Изменение: #amount#\n"
                    + "Время: #date# #time#\n"
                    + "Тип: #type#\n"
                    + "Комментарий: #comment#"));
            translationRepository.save(new Translation(lang,
                    "MASK_CASHOUT_BALANCE",
                    "Баланс #currency#: #amount#"));
            translationRepository.save(new Translation(lang,
                    "MASK_APPROVE_TRANSFER",
                    "Вы собираетесь перечислить "
                    + "#amount# #currency# пользователю @#username#. Подтвердите операцию."));
            translationRepository.save(new Translation(lang,
                    "MASK_CASHOUT_ADDRESS",
                    "Введите адрес #currency#-кошелька"));
            translationRepository.save(new Translation(lang,
                    "MASK_APPROVE_CASHOUT",
                    "Вы собираетесь перечислить "
                    + "#amount# #currency# на адрес #address#. Подтвердите операцию."));
            translationRepository.save(new Translation(lang,
                    "MASK_ICO_EXENIUM_DEPO_ADDRESS",
                    "Переведите средства на адрес:\n\n<code>#address#</code>"
                    + "\n\nТокены XNT и XNTB зачислятся автоматически"));
            translationRepository.save(new Translation(lang,
                    "MASK_ICO_EXENIUM_REF_PROGRAM",
                    "Покажите ссылку друзьям и получите 10% с их участия в виде бонусов\n"
                    + "\n"
                    + "Ссылка на телеграм:\nhttps://telegram.exenium.io/go.php?"
                    + "utm_source=referral&utm_refid=#id#\n\n"
                    + "Ссылка на сайт:\nhttps://exenium.io?"
                    + "utm_source=referral&utm_refid=#id#\n"));
            translationRepository.save(new Translation(lang,
                    "MASK_TOTAL_OFFERS",
                    "Всего предложений на бирже: #count#"));
            translationRepository.save(new Translation(lang,
                    "MASK_ICO_EXENIUM_INFO",
                    "Exenium выходит на ICO. Чем быстрее вступите - тем больше бонусов получите.\n"
                    + "<a href=\"https://exenium.io/upload/iblock/3a3/Exenium_Whitepaper_Eng.pdf\">"
                    + "Описание программы</a>\n"
                    + "<a href=\"https://medium.com/@exeniumexchange/about-bonuses-de5a3b2a3c84\">"
                    + "Как начисляются бонусы</a>\n\n"
                    + "Продано: #sold_xnt# XNT\n"
                    + "Всего бонусных: #bounty_xntb# XNTB\n"
                    + "Инвесторов: #contributors#\n"
                    + "Всего выпущено: #total_emitted# XNTB\n\n"
                    + "Вы купили: #user_xnt# XNT <a href=\"google.com\">Подробнее...</a>\n"
                    + "Ваш бонус: #user_xntb# XNTB\n"
                    + "Общий баланс: #user_total# XNT"));

            translationRepository.save(new Translation(lang,
                    "BUTTON_MAIN_MENU", "В меню"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_NEXT", ">>"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_PREV", "<<"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_WALLET", "Кошелек"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_SUPPORT", "Поддержка"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_STOCK_EXCHANGE", "Биржа"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_CASH_IN", "Пополнить"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_CASH_OUT", "Вывести"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_HISTORY", "История"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_TRANSFER", "Перевод другу"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_EXCHANGE", "Обмен валюты"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_APPROVE", "Подтвердить"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_CANCEL", "Отмена"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_OK", "OK"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_EXENIUM_ICO", "Exenium ICO"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_EXENIUM_ICO_WEB", "Exenium ICO Web"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_ICO_BUY_TOKENS", "Купить токены"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_ICO_REFERRAL_PROGRAM", "Реферральная программа"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_BACK", "Назад"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_ACCEPT", "Принять"));
            translationRepository.save(new Translation(lang,
                    "BUTTON_REJECT", "Отказаться"));

        }
    }

}
