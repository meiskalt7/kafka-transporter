package org.meiskalt7.kafka.transporter.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.meiskalt7.kafka.transporter.config.ServiceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@EnableBinding({KafkaSink.class, KafkaSource.class})
public class TransporterService {

    private final ServiceProperties serviceProperties;
    private final MessageChannel outputChannel;

    public TransporterService(ServiceProperties serviceProperties, @Qualifier(KafkaSource.OUTPUT) MessageChannel outputChannel) {
        this.serviceProperties = serviceProperties;
        this.outputChannel = outputChannel;
    }

    @StreamListener(KafkaSink.INPUT)
    public void transport(Message<byte[]> message) {

        final Acknowledgment acknowledgment = message.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);

        byte[] outputMessagePayload = message.getPayload();

        if (ArrayUtils.isEmpty(outputMessagePayload)) {
            this.sendAcknowledgment(acknowledgment);
            return;
        }

        String outputMessage = null;

        try {

            outputMessage = new String(outputMessagePayload, StandardCharsets.UTF_8.name());

            if (StringUtils.isBlank(outputMessage) || StringUtils.containsNone(outputMessage, '<', '/', '>')) {
                this.sendAcknowledgment(acknowledgment);
                return;
            }

            outputMessage = outputMessage.substring(outputMessage.indexOf('<'));

            final InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(outputMessage));

            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

            documentBuilderFactory.setNamespaceAware(true);
            final Document doc = documentBuilderFactory.newDocumentBuilder().parse(is);

            String tagName = doc.getDocumentElement().getTagName();
            if (isTagNameWithNamespace(tagName)) {
                tagName = removeNamespace(tagName);
            }

            if (serviceProperties.getTypesSet().contains(tagName)) {
                outputMessagePayload = outputMessage.getBytes(StandardCharsets.UTF_8);

                if (outputChannel.send(MessageBuilder.withPayload(outputMessagePayload).build(), serviceProperties.getSendTimeout())) {
                    log.info("Successfully sent message");
                } else {
                    throw new RuntimeException("Failed to send message \"" + outputMessage + "\"");
                }
            } else {
                log.info("Failed to send message \"" + outputMessage + "\" because of unsupported message type \"" + tagName + "\"");
            }
            this.sendAcknowledgment(acknowledgment);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException("Message is \"" + outputMessage + "\"", e);
        }
    }

    private void sendAcknowledgment(Acknowledgment acknowledgment) {
        if (acknowledgment != null) {
            acknowledgment.acknowledge();
        }
    }

    private boolean isTagNameWithNamespace(String tagName) {
        return tagName.contains(":");
    }

    private String removeNamespace(String tagName) {
        return tagName.substring(tagName.indexOf(':') + 1, tagName.length());
    }
}
