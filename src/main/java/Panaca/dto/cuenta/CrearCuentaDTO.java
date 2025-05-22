package Panaca.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CrearCuentaDTO(
        @NotBlank(message = "La cédula es requerida")
        @Length(min = 10 ,max = 10, message = "La cédula debe tener un máximo de 10 caracteres")
        @Pattern(regexp = "\\d+", message = "La cédula solo debe contener números")//La expresión regular \\d+ en @Pattern asegura que la cédula solo contenga dígitos
        String cedula,

        @NotBlank(message = "El nombre es requerido")
        @Length(max = 100, message = "El nombre debe tener un máximo de 100 caracteres")
        String nombre,

        @NotBlank(message = "El teléfono es requerido")
        @Length(max = 10, message = "El teléfono debe tener un máximo de 10 caracteres")
        @Pattern(regexp = "\\d{10}", message = "El teléfono debe contener solo números")
        String telefono,

        @NotBlank(message = "El email es requerido")
        @Length(max = 50, message = "El email debe tener un máximo de 50 caracteres")
        @Email(message = "El email no es válido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        @Length(min = 7, max = 20, message = "La contraseña debe tener un mínimo de 7 caracteres y un máximo de 20 caracteres")
        String password
) {}