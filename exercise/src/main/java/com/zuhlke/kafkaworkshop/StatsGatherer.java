package com.zuhlke.kafkaworkshop;

import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsGatherer extends Thread {
    private static final String BOOTSTRAP_SERVERS = "workshop-kafka.kafka:9092";
    private static final Logger log = LoggerFactory.getLogger(StatsGatherer.class);
    private Map<String, Long> birthsByCountry = new HashMap<>();
    
    private static final String TOPIC = "births";
    private KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Map.of(
        "bootstrap.servers", BOOTSTRAP_SERVERS,
        "group.id", "stats-gathering",
        "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
        "enable.auto.commit", "true",
        "auto.commit.interval.ms", "1000"
    ));

    public StatsGatherer() {
        
        // periodically print statistics
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> printTopTen(), 0, 10, SECONDS);
    }
    
    public static void main(String[] args) {
        new StatsGatherer().start();
    }
    
    @Override
    public void run() {
        consumer.subscribe(Arrays.asList(TOPIC));
        while (true) {
            for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(100))) {
                addBirth(Birth.parse(record.value()));
            }
        }
    }
    
    private void addBirth(Birth birth) {
        birthsByCountry.compute(birth.country, (k, v) -> v == null ? 1 : v + 1);
    }
    
    private void printTopTen() {
        log.info("Top 10 countries by babies born:");
        birthsByCountry.entrySet().stream()
            .sorted(comparing(en -> en.getValue()))
            .limit(10)
            .forEach(en -> log.info(en.getValue() + ":\t" + en.getKey()));
    }
}
