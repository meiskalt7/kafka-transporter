package org.meiskalt7.kafka.transporter;

import org.meiskalt7.kafka.transporter.config.ServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringCloudApplication
@EnableEurekaClient
@ComponentScan("org.meiskalt7.kafka.transporter")
@PropertySource("classpath:application.yml")
@EnableConfigurationProperties(ServiceProperties.class)
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
