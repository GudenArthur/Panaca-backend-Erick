package Panaca.model.documents;

import Panaca.model.enums.EstadoDevolucion;
import Panaca.model.enums.TipoDevolucion;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Document("devoluciones")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DevolucionRequest {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String idCuenta;
    private TipoDevolucion tipo;
    private String referenciaId;

    private LocalDateTime fechaSolicitud;
    private EstadoDevolucion estado;
}