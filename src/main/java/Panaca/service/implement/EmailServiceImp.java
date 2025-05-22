package Panaca.service.implement;

import Panaca.configs.SmtpProperties;
import Panaca.dto.email.EmailDTO;
import Panaca.service.service.EmailService;
import Panaca.exceptions.EmailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated

public class EmailServiceImp implements EmailService {

    private final SmtpProperties smtpProperties;

    public EmailServiceImp(SmtpProperties smtpProperties) {
        this.smtpProperties = smtpProperties;
    }

    @Override
    @Async
    public void enviarCorreo(EmailDTO emailDTO) throws EmailException {

        // Construcción del correo con formato HTML y CSS
        String htmlContent = "<!DOCTYPE html>" +
                "<html lang=\"es\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>" + emailDTO.asunto() + "</title>" +
                "<style>" +
                "body { margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #ffffff; background-image: radial-gradient(#444cf7 0.5px, #ffffff 0.5px); background-size: 10px 10px; color: #333; }" +
                "table { width: 100%; }" +
                "td { padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background-color: #f3eded; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }" +
                ".header { font-size: 24px; font-weight: bold; color: #333; text-align: center; }" +
                ".body { font-size: 16px; color: #555; line-height: 1.5; }" +
                ".footer { font-size: 14px; color: #999; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                "<tr>" +
                "<td align=\"center\">" +
                "<table role=\"presentation\" class=\"container\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
                "<tr>" +
                "<td class=\"header\">" +
                "Hola" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"body\">" +
                emailDTO.cuerpo() +  // Contenido dinámico del cuerpo del correo
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"footer\">" +
                "&copy; 2024 - UniEventos" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</body>" +
                "</html>";

        Email email = EmailBuilder.startingBlank()
                .from(smtpProperties.getUsername())
                .to(emailDTO.destinatario())
                .withSubject(emailDTO.asunto())
                .withHTMLText(htmlContent)
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer(
                        smtpProperties.getHost(),
                        smtpProperties.getPort(),
                        smtpProperties.getUsername(),
                        smtpProperties.getPassword())
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmailException("Error en el envio del correo");
        }
    }
}