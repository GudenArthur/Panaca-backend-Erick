package Panaca.dto.autenticacion;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}
