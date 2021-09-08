package com.zuhlke.kafkaworkshop.utils;

import java.util.Date;
import java.util.UUID;

import com.google.gson.Gson;

public class LifeExpectancyRequest {
    public final UUID uuid;
    public final Date date;
    public final String country;

    public LifeExpectancyRequest(UUID uuid, Date date, String country) {
        this.uuid = uuid;
        this.date = date;
        this.country = country;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    
    public static LifeExpectancyRequest parse(String json) {
        return new Gson().fromJson(json, LifeExpectancyRequest.class);
    }
}