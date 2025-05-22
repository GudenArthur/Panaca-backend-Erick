package Panaca.model.documents;

import Panaca.model.enums.AnimalType;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("donacion_items")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DonationItem {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String donationId; // referencia explícita

    private AnimalType tipoAnimal;
    private int cantidadBultos;
    private int precioUnitario;
    private int subtotal;
}
