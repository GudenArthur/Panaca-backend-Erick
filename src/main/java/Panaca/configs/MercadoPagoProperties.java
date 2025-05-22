package Panaca.configs;

import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix = "mercadopago")
@Getter
@Setter
public class MercadoPagoProperties {
    private String token;
    private String notificationUrl;
}
