package com.zuhlke.kafkaworkshop.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

public class WorldHealthOrganizationFacade {
    private final Faker faker = new Faker();
    private final Random rnd = new Random();
    
    /**
     * Get the people born since the last call.
     * 
     * @return A list of {@link Birth} objects
     */
    public List<Birth> getBirths() {
        try {
            Thread.sleep(rnd.nextInt(1000)); // fake delay
            List<Birth> births = new ArrayList<>();
            for (int i = 0; i < rnd.nextInt(100); i++) {
                Name name = faker.name();
                births.add(new Birth(name.firstName() + " " + name.lastName(), new Date(), faker.country().name()));
            }
            return births;
        } catch (InterruptedException e) {
            return Collections.emptyList();
        }
    }
}