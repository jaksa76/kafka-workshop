package com.zuhlke.kafkaworkshop;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsCombiner {    
    private static final Logger log = LoggerFactory.getLogger(StatsCombiner.class);
    private static final String BOOTSTRAP_SERVERS = "my-release-kafka.kafka:9092";
    private static final String TOPIC = "birthStats";

    public static void main(String[] args) {
        Map<String, Map<String, Long>> statsByCollector = new HashMap<>();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Map.of(
            "bootstrap.servers", BOOTSTRAP_SERVERS,
            "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
            "enable.auto.commit", "true",
            "auto.commit.interval.ms", "1000"
        ));
        consumer.subscribe(asList(TOPIC));
        
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> printStats(statsByCollector), 0, 10, TimeUnit.SECONDS);
        
        while (true) {
            while (true) {
                for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(100))) {
                    String collector = record.key();
                    Map<String, Long> stats = parse(record.value());
                    statsByCollector.put(collector, stats);
                }
            }
        }
    }
    
    private static Map<String, Long> parse(String value) {
        return new HashMap<>();
    }

    private static void printStats(Map<String, Map<String, Long>> statsByCollector) {
        Map<String, Long> aggregatedStats = new HashMap<>();        
        for (Map<String, Long> stats : statsByCollector.values()) {
            stats.forEach((country, births) -> {
                aggregatedStats.compute(country, (k, v) -> v == null ? births : v + births);
            });
        }
        
        log.info("Top 10 countries by babies born:");
        
        aggregatedStats.entrySet().stream()
            .sorted(comparing(en -> en.getValue()))
            .limit(10)
            .forEach(en -> log.info(en.getValue() + ":\t" + en.getKey()));
    }
}
