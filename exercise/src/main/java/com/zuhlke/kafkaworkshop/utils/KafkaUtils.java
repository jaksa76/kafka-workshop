package com.zuhlke.kafkaworkshop.utils;

import java.net.InetAddress;

public class KafkaUtils {
    public static final String BOOTSTRAP_SERVERS = "workshop-kafka.kafka:9092";
    
    public static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "localhost";
        }
    }

    public static String studentName() {
        String hostname = hostname();
        return hostname.substring(0, hostname.indexOf("-"));
    }
}
