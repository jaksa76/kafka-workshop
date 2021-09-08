package com.zuhlke.kafkaworkshop;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import com.zuhlke.kafkaworkshop.utils.KafkaUtils;
import com.zuhlke.kafkaworkshop.utils.LifeExpectancyRequest;
import com.zuhlke.kafkaworkshop.utils.LifeExpectancyResponse;
import com.zuhlke.kafkaworkshop.utils.LifeExpectancyTools;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class LifeExpectancyCalculator extends Thread {
    private LifeExpectancyTools tools = new LifeExpectancyTools();
    
    private static final String REQUEST_TOPIC = "life.expectancy.req";
    private static final String RESPONSE_TOPIC = "life.expectancy.resp";
    
    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Map.of(
        "bootstrap.servers", KafkaUtils.BOOTSTRAP_SERVERS,
        "group.id", "life.expectancy.calculator",
        "key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer",
        "enable.auto.commit", "true",
        "auto.commit.interval.ms", "1000"
    ));
    
    KafkaProducer<String, String> producer = new KafkaProducer<>(Map.of(
        "bootstrap.servers", KafkaUtils.BOOTSTRAP_SERVERS,
        "key.serializer", "org.apache.kafka.common.serialization.StringSerializer",
        "value.serializer", "org.apache.kafka.common.serialization.StringSerializer"
    ));
    
    public static void main(String[] args) {
        new LifeExpectancyCalculator().start();
    }
    
    @Override
    public void run() {
        consumer.subscribe(Arrays.asList(REQUEST_TOPIC));      
        while (true) {
            for (ConsumerRecord<String, String> record : consumer.poll(Duration.ofMillis(100))) {
                LifeExpectancyRequest req = LifeExpectancyRequest.parse(record.value());
                new Thread(() -> calculateLifeExpectancy(req)).start();
            }
        }  
    }
    
    private void calculateLifeExpectancy(LifeExpectancyRequest req) {
        int lifeExpectancy = tools.calculateLifeExpectancy(req.date);
        publishResponse(new LifeExpectancyResponse(req.uuid, lifeExpectancy));
    }
    
    synchronized private void publishResponse(LifeExpectancyResponse resp) {
        producer.send(new ProducerRecord<String,String>(RESPONSE_TOPIC, resp.toString()));
    }
}
