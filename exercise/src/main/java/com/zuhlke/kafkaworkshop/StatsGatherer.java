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
    
    private static final String TOPIC = "births";
    private KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Map.of(
        "bootstrap.servers", KafkaUtils.BOOTSTRAP_SERVERS,
        "group.id", KafkaUtils.hostname(),
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
                stats.addBirth(Birth.parse(record.value()));
            }
        }
    }

    private void printTopTen() {
        log.info("Top 10 countries by babies born:\n" + stats.getTopTenAsString());
    }
}
