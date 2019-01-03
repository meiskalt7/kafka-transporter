package org.meiskalt7.kafka.transporter.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.Set;

@ConfigurationProperties(prefix = "app")
@Slf4j
@Getter
@Setter
@Validated
@RefreshScope
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProperties {
    @NotEmpty
    private Set<String> typesSet;
    @Positive
    private int sendTimeout;
}
