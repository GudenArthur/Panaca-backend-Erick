package Panaca.model.documents;

import Panaca.model.vo.DetalleCarrito;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("carrito")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Carrito {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String idUsuario;
    private List<DetalleCarrito> items;

}