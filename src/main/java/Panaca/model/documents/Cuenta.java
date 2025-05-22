package Panaca.model.documents;


import Panaca.model.enums.EstadoCuenta;
import Panaca.model.enums.Rol;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document("cuentas")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cuenta {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String cedula;
    private String nombre;
    private String telefono;
    private String email;
    private String password;
    private String codigoVerificacionRegistro;
    private String codigoVerificacionContrasenia;
    private LocalDateTime fechaExpiracionCodigo;
    private LocalDateTime fechaExpiracionCodigoContrasenia;
    private LocalDateTime fechaRegistro;
    private Rol rol;
    private EstadoCuenta estado;
}