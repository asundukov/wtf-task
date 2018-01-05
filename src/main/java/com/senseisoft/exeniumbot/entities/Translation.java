package com.senseisoft.exeniumbot.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Type;

@Entity
public class Translation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    
        
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;
    
    @Type(type = "text")
    private String translationKey;    
    @Type(type = "text")
    private String translationValue;
    
    public String getTranslationKey() {
        return translationKey;
    }

    public String getTranslationValue() {
        return translationValue;
    }
    
    public Translation() {        
    }
    
    public Translation(Language language, String translationKey, String translationValue) {
        this.language = language;
        this.translationKey = translationKey;
        this.translationValue = translationValue;
    }
    
}
