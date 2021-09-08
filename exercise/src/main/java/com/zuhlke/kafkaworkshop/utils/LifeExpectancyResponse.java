package com.zuhlke.kafkaworkshop.utils;

import java.util.UUID;

import com.google.gson.Gson;

public class LifeExpectancyResponse {
    public final UUID uuid;
    public final int years;

    public LifeExpectancyResponse(UUID uuid, int years) {
        this.uuid = uuid;
        this.years = years;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    
    public static LifeExpectancyResponse parse(String json) {
        return new Gson().fromJson(json, LifeExpectancyResponse.class);
    }
}
