package Panaca.exceptions;

public class PQRException extends Exception{

    public PQRException(String message) {
        super(message);
    }

    /**
     * Se lanza cuando no se encuentra una PQR con un ID específico.
     */
    public static class PQRNotFoundException extends PQRException {
        public PQRNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * PQRInvalidStateException:
     * Se lanza cuando se intenta cambiar el estado de una PQR a uno inválido.
     */
    public static class PQRInvalidStateException extends PQRException {
        public PQRInvalidStateException(String message) {
            super(message);
        }
    }

    /**
     * PQRInvalidCategoryException:
     * Se lanza cuando se intenta asignar una categoría no válida a una PQR.
     */
    public static class PQRInvalidCategoryException extends PQRException {
        public PQRInvalidCategoryException(String message) {
            super(message);
        }
    }

    /**
     * Se lanza cuando se intenta responder una PQR que ya ha sido respondida.
     */
    public static class PQRResponseAlreadyProvidedException extends PQRException {
        public PQRResponseAlreadyProvidedException(String message) {
            super(message);
        }
    }

    /**
     * Se lanza cuando un usuario no tiene permisos para acceder o modificar una PQR.
     */
    public static class PQRUserUnauthorizedException extends PQRException {
        public PQRUserUnauthorizedException(String message) {
            super(message);
        }
    }
}
