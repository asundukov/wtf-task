package com.senseisoft.exeniumbot;

import com.senseisoft.exeniumbot.entities.Stat;
import com.senseisoft.exeniumbot.entities.UserData;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumAuthRequest;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumCashInMethods;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumCurrencies;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumDeeplinkRequest;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumICOStats;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumICOWallet;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumMarketOffers;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumMarketPairs;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransaction;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransactions;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransferRequest;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumUser;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWallet;
import com.senseisoft.exeniumbot.repositories.StatRepository;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.senseisoft.exeniumbot.Application.json;

@Service
public class ExeniumAPI {

    @Value("${bot.pagesize}")
    private int PAGE_SIZE;

    @Value("${bot.service_name}")
    private String SERVICE_NAME;

    @Value("${exeniumapi.url}")
    private String API_URL;

    @Value("${icoapi.url}")
    private String ICO_API_URL;

    @Autowired
    private StatRepository statRepository;

    public ExeniumUser auth(String telegramUserId) {
        Log.info(this, "auth: " + telegramUserId);
        return post("auth", "", new ExeniumAuthRequest(telegramUserId), ExeniumUser.class);
    }

    public void cpa(String exeniumUserId, String payload) {
        Log.info(this, "cpa: " + exeniumUserId + " | " + payload);
        post("xnt-ico/user/registration", new ExeniumDeeplinkRequest(exeniumUserId, payload));
    }

    public ExeniumWallet getWallet(String exeniumUserId) {
        Log.info(this, "getWallet: " + exeniumUserId);
        return get("user/wallet", exeniumUserId, ExeniumWallet.class);
    }

    public ExeniumICOWallet getExeniumICOWallet(UserData userData, String currency) {
        Log.info(this, "getExeniumICOWallet: " + userData.getExeniumId() + " | " + currency);
        List<Stat> statList = statRepository.findByUserDataOrderByCreationTimeDesc(userData);
        if (statList == null) {
            statList = new ArrayList<>();
        }
        Optional<Stat> stat = statList.stream().findFirst();
        return get("xnt-ico/user/wallet?user_id=" + userData.getExeniumId() + "&currency_code=" + currency
                + (stat.isPresent() ? "&stat_id=" + stat.get().getStatId() : ""),
                userData.getExeniumId(), ExeniumICOWallet.class);
    }

    public ExeniumTransactions getTransactions(String exeniumUserId, int page, int total) {
        Log.info(this, "getTransactions: " + exeniumUserId + " | " + page);
        return getPage("user/wallet/transaction/list",
                exeniumUserId, page - 1, PAGE_SIZE, ExeniumTransactions.class);
    }

    public ExeniumCurrencies getCurrencies(String exeniumUserId) {
        Log.info(this, "getCurrencies: " + exeniumUserId + " | 1000");
        return getPage("currency/list",
                exeniumUserId, 0, 1000, ExeniumCurrencies.class);
    }

    public ExeniumCashInMethods getCashInMethods(String exeniumUserId, String currency) {
        Log.info(this, "getCashInMethods: " + exeniumUserId);
        return get("user/wallet/cashin/info?currency_code=" + currency,
                exeniumUserId, ExeniumCashInMethods.class);
    }

    public ExeniumTransaction transfer(String exeniumUserId,
            String toId, String fromWallet, BigDecimal amount, String comment) {
        Log.info(this, "transfer: " + fromWallet + " -> " + toId);
        return post("user/wallet/operation/transfer", exeniumUserId,
                new ExeniumTransferRequest(toId, fromWallet, amount, comment),
                ExeniumTransaction.class);
    }

    public ExeniumMarketPairs getMarketPairs() {
        Log.info(this, "getMarketPairs");
        return getPage("stock-exchange/market/pairs", "", 0, 1000, ExeniumMarketPairs.class);
    }

    public int getMarketOffersCount() {
        Log.info(this, "getMarketOffersCount");
        ExeniumMarketOffers offers = getPage("user/stock-exchange/offer/list", "", 0, 1,
                ExeniumMarketOffers.class);
        return offers.total;
    }
    
    public ExeniumICOStats getICOStats() {
        Log.info(this, "getICOStats");
        return get("ico-stat", "", ExeniumICOStats.class);
    }

    private <T> T post(String method, String userId, Object data, Class<T> type) {
        try {
            HttpPost post = new HttpPost(getURL(method));
            EntityBuilder eb = EntityBuilder.create();
            eb.setContentType(ContentType.create("application/json", "utf-8"));
            eb.setText(json.writeValueAsString(data));
            post.setEntity(eb.build());
            post.setHeader("uid", userId);
            post.setHeader("esc", SERVICE_NAME);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CloseableHttpResponse response = httpClient.execute(post);
            String body = Application.getStringContent(response);
            T result = Application.json.readValue(body, type);
            response.close();
            Log.info(this, body);
            return result;
        } catch (Exception e) {
            Log.exception(e);
        }
        return null;
    }

    private void post(String method, Object data) {
        try {
            HttpPost post = new HttpPost(getURL(method));
            EntityBuilder eb = EntityBuilder.create();
            eb.setContentType(ContentType.create("application/json", "utf-8"));
            eb.setText(json.writeValueAsString(data));
            Log.info(this, json.writeValueAsString(data));
            post.setEntity(eb.build());
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CloseableHttpResponse response = httpClient.execute(post);
            response.close();
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    private <T> T get(String method, String userId, Class<T> type) {
        try {
            HttpGet get = new HttpGet(getURL(method));
            get.setHeader("uid", userId);
            get.setHeader("esc", SERVICE_NAME);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CloseableHttpResponse response = httpClient.execute(get);
            String body = Application.getStringContent(response);
            T result = Application.json.readValue(body, type);
            response.close();
            Log.info(this, body);
            return result;
        } catch (Exception e) {
            Log.exception(e);
        }
        return null;
    }

    private <T> T getPage(String method, String userId, int page, int size, Class<T> type) {
        try {
            HttpGet get = new HttpGet(getURL(method) + "?page=" + page + "&size=" + size);
            get.setHeader("uid", userId);
            get.setHeader("esc", SERVICE_NAME);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            CloseableHttpResponse response = httpClient.execute(get);
            String body = Application.getStringContent(response);
            T result = Application.json.readValue(body, type);
            response.close();
            Log.info(this, body);
            return result;
        } catch (Exception e) {
            Log.exception(e);
        }
        return null;
    }

    private String getURL(String method) {
        if (method.contains("xnt-ico")) {
            return ICO_API_URL.endsWith("/")
                    ? ICO_API_URL + method
                    : ICO_API_URL + "/" + method;
        }
        if (method.contains("ico-stat")) {
            return "https://ico-stat.exenium.io/";
        }
        return API_URL.endsWith("/")
                ? API_URL + method
                : API_URL + "/" + method;
    }
}
