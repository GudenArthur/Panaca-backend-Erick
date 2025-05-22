package Panaca.exceptions;

public class CuponException extends Exception {

    //Metodo que permite enviar mensaje de error mediante una excepcion
    public CuponException(String message) {
        super(message);
    }
}