package Panaca.service.service;

import Panaca.dto.email.EmailDTO;
import Panaca.exceptions.EmailException;
public interface EmailService {

    void enviarCorreo(EmailDTO emailDTO) throws EmailException;

}
