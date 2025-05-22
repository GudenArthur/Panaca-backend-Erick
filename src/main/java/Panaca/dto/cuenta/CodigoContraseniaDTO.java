package Panaca.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CodigoContraseniaDTO(

        @NotBlank(message = "El email es requerido")
        @Length(max = 50, message = "El email debe tener un máximo de 50 caracteres")
        @Email(message = "El email no es válido")
        String email
) {}