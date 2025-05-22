package Panaca.dto.donation;

import Panaca.model.enums.AnimalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DonationItemDTO(

        @NotNull(message = "Debe indicar el tipo de animal a donar")
        AnimalType tipoAnimal,

        @Min(value = 1, message = "Debe donar al menos un bulto")
        int cantidadBultos

) {}