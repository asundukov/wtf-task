package com.senseisoft.exeniumbot.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Stat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userdata_id")
    private UserData userData;
    
    private String statId;
    private long creationTime;

    public String getStatId() {
        return statId;
    }
    
    public Stat() {}
    
    public Stat(String statId, UserData userData) {
        this.statId = statId;
        this.userData = userData;
        this.creationTime = new Date().getTime();
    }
}
