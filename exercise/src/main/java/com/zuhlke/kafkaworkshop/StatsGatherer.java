package com.zuhlke.kafkaworkshop;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import com.zuhlke.kafkaworkshop.utils.Birth;
import com.zuhlke.kafkaworkshop.utils.BirthStats;
import com.zuhlke.kafkaworkshop.utils.KafkaUtils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of total births by country.
 */
public class StatsGatherer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(StatsGatherer.class);
    private BirthStats stats = new BirthStats();
    private String name;
    
    private static final String TOPIC = "births";
    private KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Map.of(
        "bootstrap.servers", KafkaUtils.BOOTSTRAP_SERVERS,
        "group.id", KafkaUtils.hostname(),
        "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
        "enable.auto.commit", "false"        
    ));

    private static final String STATS_TOPIC = "birth.stats";
    private KafkaProducer<String, String> producer = new KafkaProducer<>(Map.of(
        "bootstrap.servers", KafkaUtils.BOOTSTRAP_SERVERS,
        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer",
        "acks", "all",
        "enable.idempotence", "true",
        "transactional.id", KafkaUtils.hostname() + "-gatherer-" + name
    ));

    public StatsGatherer(String name) {
        // the name now has to be the same even if the gatherer is restarted
        this.name = name;
        
        // periodically print statistics
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> printTopTen(), 0, 10, SECONDS);
    }
    
    public static void main(String[] args) {
        if (args.length < 1) throw new IllegalArgumentException("Please supply the gatherer name as first argument.");        
        new StatsGatherer(args[0]).start();
    }
    
    @Override
    public void run() {
        consumer.subscribe(Arrays.asList(TOPIC));
        producer.initTransactions();
        while (true) {
            producer.beginTransaction();
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                stats.addBirth(Birth.parse(record.value()));
            }
            producer.sendOffsetsToTransaction(getOffsetsToCommit(records), STATS_TOPIC);
            producer.commitTransaction();
        }
    }

    private Map<TopicPartition, OffsetAndMetadata> getOffsetsToCommit(ConsumerRecords<String, String> records) {
        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
        for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, String>> partitionedRecords = records.records(partition);
            long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();

            offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
        }
        return offsetsToCommit;
    }
    
    private void printTopTen() {
        log.info("Top 10 countries by babies born:\n" + stats.getTopTenAsString());
        producer.send(new ProducerRecord<String,String>(STATS_TOPIC, name, stats.toString()));
    }
}
