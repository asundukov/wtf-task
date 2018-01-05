package com.senseisoft.exeniumbot.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import org.hibernate.annotations.Type;

@Entity
public class Settings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Type(type="text")
    private String textSelectLanguage;
    
    @Type(type="text")
    private String textAuthError;

    public String getTextSelectLanguage() {
        return textSelectLanguage;
    }
    
    public String getTextAuthError() {
        return textAuthError;
    }

    @PrePersist
    void preInsert() {
        if (textSelectLanguage == null) {
            textSelectLanguage = "Select your language | Выберите ваш язык";
        }
        
        if (textAuthError == null) {
            textAuthError = "Authorization error. Try pressing /start again.";
        }
    }

    public Settings() {
    }

}
