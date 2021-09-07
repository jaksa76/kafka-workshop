package com.zuhlke.kafkaworkshop;

import java.util.Date;

import com.google.gson.Gson;

public class Birth {
    public final String name;
    public final Date time;
    public final String country;    
    
    public Birth(String name, Date time, String country) {
        this.name = name;
        this.time = time;
        this.country = country;
    }
    
    @Override public String toString() { 
        return new Gson().toJson(this);
    }
    
    public static Birth parse(String s) {
        return new Gson().fromJson(s, Birth.class);        
    }
}
