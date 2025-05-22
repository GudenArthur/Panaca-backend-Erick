package Panaca.dto.donation;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DonationDTO(

        @NotBlank(message = "El ID del donante es requerido")
        String donanteId,

        @NotEmpty(message = "Debe incluir al menos un ítem en la donación")
        List<@Valid DonationItemDTO> items

) {}