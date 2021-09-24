package com.zuhlke.kafkaworkshop;

import java.util.Map;

import com.zuhlke.kafkaworkshop.utils.Birth;
import com.zuhlke.kafkaworkshop.utils.KafkaUtils;
import com.zuhlke.kafkaworkshop.utils.WorldHealthOrganizationFacade;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirthAnnouncer extends Thread {
    private static final Logger log = LoggerFactory.getLogger(BirthAnnouncer.class);
    private WorldHealthOrganizationFacade who = new WorldHealthOrganizationFacade();
    
    private static final String TOPIC = KafkaUtils.studentName() + ".births";

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
    }
}
