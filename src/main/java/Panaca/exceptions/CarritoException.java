package Panaca.exceptions;

public class CarritoException extends Exception{
    //Metodo que permite enviar mensaje de error mediante una excepcion
    public CarritoException(String message) {
        super(message);
    }

    // Excepción cuando no se encuentra el carrito por su ID
    public static CarritoException carritoNoEncontrado(String idCarrito) {
        return new CarritoException("El carrito con ID " + idCarrito + " no fue encontrado.");
    }

    // Excepción cuando no se puede eliminar el carrito
    public static CarritoException carritoNoEliminado(String idCarrito) {
        return new CarritoException("El carrito con ID " + idCarrito + " no pudo ser eliminado.");
    }

    // Excepción cuando no se puede actualizar el carrito
    public static CarritoException carritoNoActualizado(String idCarrito) {
        return new CarritoException("El carrito con ID " + idCarrito + " no pudo ser actualizado.");
    }

    // Excepción cuando no se pueden agregar o modificar los ítems del carrito
    public static CarritoException errorAlModificarItems(String idCarrito) {
        return new CarritoException("Ocurrió un error al modificar los ítems del carrito con ID " + idCarrito + ".");
    }

    // Excepción cuando no se puede vaciar el carrito
    public static CarritoException carritoNoVaciado(String idCarrito) {
        return new CarritoException("El carrito con ID " + idCarrito + " no pudo ser vaciado.");
    }

    // Excepción cuando no hay disponibilidad de algún ítem en el carrito
    public static CarritoException itemsNoDisponibles(String idCarrito) {
        return new CarritoException("Algunos ítems del carrito con ID " + idCarrito + " no están disponibles en inventario.");
    }

    // Excepción cuando se intenta finalizar una compra sin todos los datos requeridos
    public static CarritoException errorAlFinalizarCompra(String idCarrito) {
        return new CarritoException("Ocurrió un error al finalizar la compra del carrito con ID " + idCarrito + ".");
    }

    // Excepción cuando se intenta clonar un carrito que no existe
    public static CarritoException carritoNoClonado(String idCarrito) {
        return new CarritoException("El carrito con ID " + idCarrito + " no pudo ser clonado.");
    }

    // Excepción genérica para cualquier otro error relacionado con el carrito
    public static CarritoException errorGenerico(String mensaje) {
        return new CarritoException("Error en el carrito: " + mensaje);
    }
}
