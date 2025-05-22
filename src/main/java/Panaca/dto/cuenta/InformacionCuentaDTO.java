package Panaca.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record InformacionCuentaDTO(

        @NotNull(message = "Usuario no encontrado")
        String id,

        @NotBlank(message = "La cédula es requerida")
        @Length(max = 10, message = "La cédula debe tener un máximo de 10 caracteres")
        String cedula,

        @NotBlank(message = "El nombre es requerido")
        @Length(max = 100, message = "El nombre debe tener un máximo de 100 caracteres")
        String nombre,

        @NotBlank(message = "El teléfono es requerido")
        @Length(max = 10, message = "El teléfono debe tener un máximo de 10 caracteres")
        String telefono,

        @NotBlank(message = "El email es requerido")
        @Length(max = 50, message = "El email debe tener un máximo de 50 caracteres")
        @Email(message = "El email no es válido")
        String email
) {}