package Panaca.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Configuration
@ConfigurationProperties(prefix = "smtp")
@Getter
@Setter
public class SmtpProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}
