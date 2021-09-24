package com.zuhlke.kafkaworkshop;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;

import com.zuhlke.kafkaworkshop.utils.Birth;
import com.zuhlke.kafkaworkshop.utils.BirthStats;
import com.zuhlke.kafkaworkshop.utils.KafkaUtils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of total births by country.
 */
public class StatsGatherer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(StatsGatherer.class);
    private BirthStats stats = new BirthStats();
    
    private static final String TOPIC = KafkaUtils.studentName() + ".births";

    public StatsGatherer() {
        // periodically print statistics
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> printTopTen(), 0, 10, SECONDS);
    }
    
    public static void main(String[] args) {
        new StatsGatherer().start();
    }
    
    @Override
    public void run() {
        // TODO: collect births from Kafka
    }

    private void printTopTen() {
        log.info("Top 10 countries by babies born:\n" + stats.getTopTenAsString());
    }
}
