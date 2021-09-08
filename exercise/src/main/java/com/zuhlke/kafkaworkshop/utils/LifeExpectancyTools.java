package com.zuhlke.kafkaworkshop.utils;

import java.util.Date;
import java.util.Random;

public class LifeExpectancyTools {
    private Random rnd = new Random();
    
    public int calculateLifeExpectancy(Date date) {
        try {
            Thread.sleep(5_000 + rnd.nextInt(5_000));
        } catch (InterruptedException e) {}
        return rnd.nextFloat() < 0.01 ? rnd.nextInt(120) : 70 + rnd.nextInt(20);
    }
}