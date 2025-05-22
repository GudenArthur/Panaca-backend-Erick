package Panaca.model.documents;

import Panaca.model.vo.DetalleOrden;
import Panaca.model.vo.Pago;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document("ordenes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Orden {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private ObjectId idCliente;
    private String codigoCupon;
    private LocalDate fecha;
    private String codigoPasarela;
    private Pago pago;
    private List<DetalleOrden> detalle;
    private double total;

}