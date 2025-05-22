package Panaca.model.vo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetalleCarrito {

    private String idEvento;
    private int cantidad;
    private LocalDate fechaUso;
}