package com.senseisoft.exeniumbot;

import com.senseisoft.exeniumbot.entities.UserData;


public class InputProcessingResult {
    public String nextScreen;
    public UserData userData;
    public boolean update;
    
    public InputProcessingResult(String nextScreen, UserData userData, boolean update) {
        this.nextScreen = nextScreen;
        this.userData = userData;
        this.update = update;
    }
}
