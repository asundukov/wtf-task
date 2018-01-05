package com.senseisoft.exeniumbot.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Language implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String code;
    private String locale;

    private String flag;

    public Language() {
    }

    public Language(String name, String code, String locale, String flag) {
        this.name = name;
        this.code = code;
        this.flag = flag;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFlag() {
        return flag;
    }

    public String getLocale() {
        return locale;
    }

}
