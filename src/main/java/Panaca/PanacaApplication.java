package Panaca;

import Panaca.configs.CloudinaryProperties;
import Panaca.configs.MercadoPagoProperties;
import Panaca.configs.SmtpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
@EnableConfigurationProperties({
        SmtpProperties.class,
        CloudinaryProperties.class,
        MercadoPagoProperties.class})
@SpringBootApplication
public class PanacaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PanacaApplication.class, args);
    }

}
