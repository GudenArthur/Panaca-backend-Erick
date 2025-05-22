package Panaca.model.vo;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DetalleOrden {

    private ObjectId idEvento;
    private int cantidad;
    private LocalDate fechaUso;
}