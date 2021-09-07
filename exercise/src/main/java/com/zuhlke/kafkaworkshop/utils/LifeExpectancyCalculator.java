package com.zuhlke.kafkaworkshop.utils;

import java.util.Random;

public class LifeExpectancyCalculator {
    private Random rnd = new Random();
    
    public double calculateLifeExpectancy() {
        try {
            Thread.sleep(5_000 + rnd.nextInt(5_000));
        } catch (InterruptedException e) {}
        return rnd.nextFloat() < 0.01 ? rnd.nextInt(120) : 70 + rnd.nextInt(20);
    }
}