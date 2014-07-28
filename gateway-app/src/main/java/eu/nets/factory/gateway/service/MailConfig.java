package eu.nets.factory.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ComponentScan(basePackageClasses = MailConfig.class)
public class MailConfig {

    // TODO: Add value from a properties file - @Value breaks tests
    @Value("${email.host:localhost}" )
    private String host;

    // TODO: Add value from a properties file - @Value breaks tests
    @Value("${email.port:25}")
    private Integer port;

    @Value("${email.address.from:haproxy@nets.eu}")
    private String fromAddress;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);
        javaMailSender.setPort(port);

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        return properties;
    }

    public String getFromAddress() {
        return fromAddress;
    }
}
