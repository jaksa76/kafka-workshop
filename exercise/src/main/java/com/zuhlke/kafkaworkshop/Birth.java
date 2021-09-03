package com.zuhlke.kafkaworkshop;

import java.util.Date;

public class Birth {
    public final String name;
    public final Date time;
    public final String country;    
    
    public Birth(String name, Date time, String country) {
        this.name = name;
        this.time = time;
        this.country = country;
    }
    
    @Override public String toString() { return String.join(",", name, country, "" + time.getTime()); }
    
    public static Birth parse(String s) {
        String[] parts = s.split(",");
        return new Birth(parts[0], new Date(Long.valueOf(parts[1])), parts[2]);
    }
}
