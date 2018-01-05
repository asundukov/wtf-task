package com.senseisoft.exeniumbot;

import com.senseisoft.exeniumbot.entities.Language;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Translations {

    private final Language language;
    private final Map<String, String> translations;

    public String get(String key) {
        if (translations.containsKey(key)) {
            return translations.get(key);
        }
        Log.warn(this, "No key found: " + key + " for " + language.getName());
        return key;
    }

    public String getFloat(BigDecimal n) {
        Format format = NumberFormat.getNumberInstance(
                new Locale.Builder().setLanguageTag(language.getLocale()).build());
        return format.format(n);
    }

    public String getSignedFloat(BigDecimal n) {
        return n.signum() <= 0 ? getFloat(n) : "+" + getFloat(n);
    }

    public String getDate(long timestamp) {
        Locale locale = new Locale.Builder().setLanguageTag(language.getLocale()).build();
        Date date = new Date(timestamp);
        return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(date);
    }
    
    public String getLongDate(long timestamp) {
        Locale locale = new Locale.Builder().setLanguageTag(language.getLocale()).build();
        Date date = new Date(timestamp);
        return DateFormat.getDateInstance(DateFormat.LONG, locale).format(date);
    }

    public String getTime(long timestamp) {
        Locale locale = new Locale.Builder().setLanguageTag(language.getLocale()).build();
        Date date = new Date(timestamp);
        return DateFormat.getTimeInstance(DateFormat.SHORT, locale).format(date);
    }
    
    public String getLongTime(long timestamp) {
        Locale locale = new Locale.Builder().setLanguageTag(language.getLocale()).build();
        Date date = new Date(timestamp);
        return DateFormat.getTimeInstance(DateFormat.LONG, locale).format(date);
    }

    public Translations(Language language, Map<String, String> translations) {
        this.language = language;
        this.translations = translations;
    }
}
