package Panaca.configs;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

public class AuthUtils {

    public static String obtenerIdUsuarioDesdeToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        Object id = authentication.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("id:"))
                .map(auth -> auth.getAuthority().substring(3))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("ID no presente en el token"));

        return id.toString();
    }
}
