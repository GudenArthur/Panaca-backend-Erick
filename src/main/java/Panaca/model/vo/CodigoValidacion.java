package Panaca.model.vo;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CodigoValidacion {

    private LocalDateTime fechaCreacion;
    private String Codigo;

}
