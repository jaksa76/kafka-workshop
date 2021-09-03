package com.zuhlke.kafkaworkshop;

import java.util.Map;
import java.util.Properties;

import com.zuhlke.kafkaworkshop.utils.WorldHealthOrganizationFacade;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirthAnnouncer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(BirthAnnouncer.class);
    private WorldHealthOrganizationFacade who = new WorldHealthOrganizationFacade();
    
    private static final String BOOTSTRAP_SERVERS = "my-release-kafka.kafka:9092";
    private static final String TOPIC = "births";
    private KafkaProducer<String, String> producer = new KafkaProducer<>(Map.of(
        "bootstrap.servers", BOOTSTRAP_SERVERS,
        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer"
    ));

    public static void main(String[] args) {
        new BirthAnnouncer().start();
    }
    
    @Override
    public void run() {
        while (true) {
            for (Birth birth : who.getBirths()) {                
                announceBirth(birth);
            }
        }
    }
    
    private void announceBirth(Birth birth) {
        log.info("{} was born in {} on {}", birth.name, birth.country, birth.time.toString());        
        // producer.send(new ProducerRecord<String, String>(topic, birth.toString()));
    }
}
