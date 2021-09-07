package com.zuhlke.kafkaworkshop.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class BirthStats {
    private Map<String, Long> birthsByCountry = new HashMap<>();
    
    synchronized public void addBirth(Birth birth) {
        birthsByCountry.compute(birth.country, (k, v) -> v == null ? 1 : v + 1);
    }

    synchronized public void addAll(BirthStats other) {
        other.birthsByCountry.forEach((country, births) -> {
            birthsByCountry.compute(country, (k, v) -> v == null ? births : v + births);
        });
    }

    synchronized public String printTopTen() {
        Comparator<Map.Entry<String, Long>> comparator = Comparator.comparing(en -> en.getValue());
        StringBuilder stats = new StringBuilder();
        birthsByCountry.entrySet().stream()
            .sorted(comparator.reversed())
            .limit(10)
            .forEach(en -> stats.append(en.getKey() + ":\t" + en.getValue()+"\n"));
        return stats.toString();
    }

    @Override
    synchronized public String toString() {
        return new Gson().toJson(this);
    }

    public static BirthStats parse(String json) {        
        return new Gson().fromJson(json, BirthStats.class);
    }
}
