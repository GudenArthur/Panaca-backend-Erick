package Panaca.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CambiarPasswordDTO(

        @NotBlank(message = "El codigo esta vacio")
        String codigoVerificacion,

        @NotBlank(message = "La contraseña es requerida")
        @Length(min = 7, max = 20, message = "La contraseña debe tener un mínimo de 7 caracteres y un máximo de 20 caracteres")
        String passwordNueva
) {}