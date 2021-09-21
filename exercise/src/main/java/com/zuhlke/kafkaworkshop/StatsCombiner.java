package com.zuhlke.kafkaworkshop;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.zuhlke.kafkaworkshop.utils.BirthStats;
import com.zuhlke.kafkaworkshop.utils.KafkaUtils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatsCombiner {    
    private static final Logger log = LoggerFactory.getLogger(BirthStats.class);
    private static final String TOPIC = "birth.stats";

    public static void main(String[] args) {
        Map<String, BirthStats> statsByCollector = new HashMap<>();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Map.of(
            "bootstrap.servers", KafkaUtils.BOOTSTRAP_SERVERS,
            "group.id", "combiner",
            "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
            "enable.auto.commit", "false"
        ));
        consumer.subscribe(asList(TOPIC));
        
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> printStats(statsByCollector), 0, 10, TimeUnit.SECONDS);
        
        while (true) {
            for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(100))) {
                String collector = record.key();
                BirthStats stats = BirthStats.parse(record.value());
                statsByCollector.put(collector, stats);
            }
            consumer.commitSync();
        }
    }
    
    private static void printStats(Map<String, BirthStats> statsByCollector) {
        BirthStats aggregatedStats = new BirthStats();                
        statsByCollector.values().stream().forEach(aggregatedStats::addAll);
        log.info("Aggregated top 10 countries by babies born:\n" + aggregatedStats.getTopTenAsString());
    }
}
