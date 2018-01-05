package com.senseisoft.exeniumbot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senseisoft.exeniumbot.telegram.Update;
import com.senseisoft.exeniumbot.telegram.UpdateResponse;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "com.senseisoft.exeniumbot.repositories")
@ComponentScan("com.senseisoft.exeniumbot")
@PropertySource("file:${settingsDir}/application.properties")
public class Application {

    @Value("${bot.pagesize}")
    private int pageSize;
    public static int PAGE_SIZE;

    @Value("${bot.url}")
    private String botUrl;
    public static String URL;

    @Value("${telegram.token}")
    private String telegramToken;
    public static String TELEGRAM_API_TOKEN;

    @Value("${bot.screen.ico}")
    private boolean scrICO;
    public static boolean SCREEN_ICO;

    @Value("${bot.screen.exchange}")
    private boolean scrExchange;
    public static boolean SCREEN_EXCHANGE;

    @Value("${bot.screen.transfer}")
    private boolean scrTransfer;
    public static boolean SCREEN_TRANSFER;

    @Value("${bot.screen.cashin}")
    private boolean scrCashIn;
    public static boolean SCREEN_CASHIN;

    @Value("${bot.screen.cashout}")
    private boolean scrCashOut;
    public static boolean SCREEN_CASHOUT;

    @Value("${bot.ico.eth}")
    private boolean icoETH;
    public static boolean ICO_ETH;
    @Value("${bot.ico.btc}")
    private boolean icoBTC;
    public static boolean ICO_BTC;
    @Value("${bot.ico.bch}")
    private boolean icoBCH;
    public static boolean ICO_BCH;
    @Value("${bot.ico.etc}")
    private boolean icoETC;
    public static boolean ICO_ETC;

    @Value("${cabinet.url}")
    private String cabinetURL;
    public static String CABINET_URL;

    @Value("${cabinet.salt}")
    private String cabinetSalt;
    public static String CABINET_SALT;

    @Value("${bot.welcome_video}")
    private String welcomeVideo;
    public static String WELCOME_VIDEO;

    @Value("${bot.add_pair_url}")
    private String addPairURL;
    public static String ADD_PAIR_URL;

    public static final Logger log = LoggerFactory.getLogger(Application.class);
    public static final ObjectMapper json
            = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private BotController controller;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        PAGE_SIZE = pageSize;
        URL = botUrl;
        TELEGRAM_API_TOKEN = telegramToken;
        SCREEN_ICO = scrICO;
        SCREEN_TRANSFER = scrTransfer;
        SCREEN_EXCHANGE = scrExchange;
        SCREEN_CASHIN = scrCashIn;
        SCREEN_CASHOUT = scrCashOut;
        ICO_ETH = icoETH;
        ICO_BTC = icoBTC;
        ICO_BCH = icoBCH;
        ICO_ETC = icoETC;
        CABINET_URL = cabinetURL;
        CABINET_SALT = cabinetSalt;
        WELCOME_VIDEO = welcomeVideo;
        ADD_PAIR_URL = addPairURL;
        TelegramAPI.setWebhook();
    }

    @PostMapping("/${telegram.token}")
    @ResponseBody
    public Callable<UpdateResponse> update(@RequestBody String body) {
        return ()
                -> {
            Log.info(this, body);
            try {
                List<Update> updates = new ArrayList<>();
                if (body.startsWith("[")) {
                    updates = json.readValue(body, new TypeReference<List<Update>>() {
                    });
                } else {
                    updates.add(json.readValue(body, new TypeReference<Update>() {
                    }));
                }

                if (updates == null) {
                    return new UpdateResponse();
                }

                for (Update update : updates) {
                    controller.handle(update);
                }

                return new UpdateResponse();
            } catch (Exception e) {
                Log.exception(e);
                return new UpdateResponse();
            }
        };
    }

    @PostMapping("/push")
    @ResponseBody
    public Callable<UpdateResponse> push(@RequestBody String body) {
        return ()
                -> {
            try {
                List<PushRequest> pushes = new ArrayList<>();
                if (body.startsWith("[")) {
                    pushes = json.readValue(body,
                            new TypeReference<List<PushRequest>>() {
                    });
                } else {
                    pushes.add(json.readValue(body, new TypeReference<PushRequest>() {
                    }));
                }

                if (pushes == null) {
                    return new UpdateResponse();
                }

                for (PushRequest push : pushes) {
                    controller.push(push);
                }

                return new UpdateResponse();
            } catch (Exception e) {
                Log.exception(e);
                return new UpdateResponse();
            }
        };
    }

    public static String getStringContent(CloseableHttpResponse response) throws IOException {
        BufferedReader bufferedReader
                = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
        String body;
        String content = "";

        while ((body = bufferedReader.readLine()) != null) {
            content += body + "\n";
        }
        return content.trim();
    }

}
