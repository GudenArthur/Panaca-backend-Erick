package Panaca.dto.PQR;

import Panaca.model.enums.CategoriaPQR;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CrearPQRDTO(

        @NotBlank(message = "El ID del usuario es obligatorio.")
        String idUsuario,

        @NotNull(message = "La categoría PQR es obligatoria.")
        CategoriaPQR categoriaPQR,

        @NotBlank(message = "La descripción de la PQR es obligatoria.")
        @Length(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres.")
        String descripcion
) {}