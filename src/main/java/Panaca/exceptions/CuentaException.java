package Panaca.exceptions;

public class CuentaException extends Exception{

    //Metodo que permite enviar mensaje de error mediante una excepcion
    public CuentaException(String message) {
        super(message);
    }

}
