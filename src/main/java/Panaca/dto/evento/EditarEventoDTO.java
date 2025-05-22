package Panaca.dto.evento;

import Panaca.model.enums.EstadoEvento;
import Panaca.model.enums.TipoEvento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record EditarEventoDTO(

        @NotBlank(message = "Debe agregar un nombre a el evento")
        @Length(min= 5, max=100,message = "El evento debe tener un nombre maximo a 100 caracteres")
        String nombre,

        @NotBlank(message = "Debe agergar una descripcion a el evento")
        @Length(min=5,max=250,message = "La descripcion tiene un maximo de 250 caracteres")
        String descripcion,

        @NotNull(message = "Debe agregar una imagen para la portada del evento")
        String imagenPortada,

        @NotNull(message = "Debe seleccionar el estado del evento")
        EstadoEvento estadoEvento,

        @NotNull(message = "Debe seleccionar el tipo de evento")
        TipoEvento tipoEvento,

        @NotNull(message = "El precio de la boleta es obligatoria.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El total debe ser mayor a 0.")
        Float precio

) {}