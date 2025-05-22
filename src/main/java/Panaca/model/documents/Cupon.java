package Panaca.model.documents;

import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("cupones")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cupon {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String nombre;
    private String codigo;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime fechaApertura;
    private float descuento;
    private TipoCupon tipo;
    private EstadoCupon estado;

    private boolean utilizado;

    public Cupon(String nombre, String codigo, LocalDateTime fechaVencimiento, LocalDateTime fechaApertura, float descuento, TipoCupon tipo, EstadoCupon estado, boolean utilizado) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaApertura = fechaApertura;
        this.descuento = descuento;
        this.tipo = tipo;
        this.estado = estado;
        this.utilizado = utilizado;
    }
}
