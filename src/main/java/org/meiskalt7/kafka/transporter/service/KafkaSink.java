package org.meiskalt7.kafka.transporter.service;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface KafkaSink {

    String INPUT = "input";

    @Input(INPUT)
    SubscribableChannel input();

}
