package Panaca.dto.donation;

import java.time.LocalDateTime;
import java.util.List;

public record MostrarDonacionDTO(
        String id,
        LocalDateTime fecha,
        int total,
        List<ItemDonacionDTO> items
) {}