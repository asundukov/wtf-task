package com.senseisoft.exeniumbot.entities;

import com.senseisoft.exeniumbot.Application;
import com.senseisoft.exeniumbot.Log;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumTransactions;
import com.senseisoft.exeniumbot.exeniumapi.ExeniumWallet;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Type;

@Entity
public class UserData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int messageId;

    private String telegramId;
    private String telegramUsername;

    private String currentScreen;

    private String language;
    private String exeniumId;
    private String refId;

    @Type(type = "text")
    private String dataCache;
    @Type(type = "text")
    private String inputCache;

    private int page;

    private boolean newRegistration;

    public UserData() {
    }

    public UserData(String telegramId, String telegramUsername, String language) {
        this.telegramId = telegramId;
        this.telegramUsername = telegramUsername;
        this.language = language;
        try {
            this.inputCache = Application.json.writeValueAsString(new InputCache());
        } catch (Exception e) {
            Log.exception(e);
        }
        this.newRegistration = true;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefId() {
        return refId;
    }

    public boolean isNewRegistration() {
        return newRegistration;
    }

    public void setNewRegistration(boolean newRegistration) {
        this.newRegistration = newRegistration;
    }

    public String getExeniumId() {
        return exeniumId;
    }

    public void setExeniumId(String exeniumId) {
        this.exeniumId = exeniumId;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public String getLanguage() {
        return language;
    }

    public String getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(String currentScreen) {
        this.currentScreen = currentScreen;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public DataCache getDataCache() {
        if (dataCache == null) {
            return new DataCache();
        }
        try {
            return Application.json.readValue(dataCache, DataCache.class);
        } catch (Exception e) {
            Log.exception(e);
            return new DataCache();
        }
    }

    public void setDataCache(DataCache dataCache) {
        try {
            this.dataCache = Application.json.writeValueAsString(dataCache);
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    public ExeniumWallet getWalletCache() {
        DataCache cache = getDataCache();
        return cache.wallet;
    }

    public void setWalletCache(ExeniumWallet wallet) {
        DataCache cache = getDataCache();
        cache.wallet = wallet;
        setDataCache(cache);
    }

    public void setTransactionsCache(ExeniumTransactions transactions) {
        DataCache cache = getDataCache();
        cache.transactions = transactions;
        setDataCache(cache);
    }

    public ExeniumTransactions getTransactionsCache() {
        DataCache cache = getDataCache();
        return cache.transactions;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getInput(String key) {
        try {
            InputCache cache = Application.json.readValue(inputCache, InputCache.class);
            return cache.cache.containsKey(key) ? cache.cache.get(key) : "";
        } catch (Exception e) {
            Log.exception(e);
            return "";
        }
    }

    public void setInput(String key, String value) {
        try {
            InputCache cache = Application.json.readValue(inputCache, InputCache.class);
            cache.cache.put(key, value);
            this.inputCache = Application.json.writeValueAsString(cache);
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    public void flushInput(String key) {
        try {
            InputCache cache = Application.json.readValue(inputCache, InputCache.class);
            cache.cache.remove(key);
            this.inputCache = Application.json.writeValueAsString(cache);
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    public void flushInput() {
        try {
            this.inputCache = Application.json.writeValueAsString(new InputCache());
        } catch (Exception e) {
            Log.exception(e);
        }
    }

    public boolean hasInput(String key) {
        return getInput(key).length() > 0;
    }

}
