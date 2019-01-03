package org.meiskalt7.kafka.transporter.service;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KafkaSource {

    String OUTPUT = "output";

    @Output(OUTPUT)
    MessageChannel output();

}
