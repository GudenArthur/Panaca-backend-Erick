package Panaca.test;

import Panaca.dto.orden.DetalleOrdenDTO;
import Panaca.dto.orden.EditarOrdenDTO;
import Panaca.dto.orden.MostrarDetalleOrdenDTO;
import Panaca.dto.orden.MostrarOrdenDTO;
import Panaca.exceptions.CarritoException;
import Panaca.exceptions.OrdenException;
import Panaca.model.documents.*;
import Panaca.model.enums.*;
import Panaca.model.vo.DetalleCarrito;
import Panaca.model.vo.DetalleOrden;
import Panaca.model.vo.Pago;
import Panaca.repository.*;
import Panaca.service.service.OrdenService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.validation.ConstraintViolationException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrdenServiceTest {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private CuponRepository cuponRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    private Cuenta cuenta;
    private Evento evento;
    private Orden orden;
    private Carrito carrito;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setNombre("Usuario Test");
        cuenta.setEmail("orden@test.com");
        cuenta.setTelefono("3001234567");
        cuenta.setPassword("clave");
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta = cuentaRepository.save(cuenta);
        assertNotNull(cuenta.getId());

        evento = new Evento();
        evento.setNombre("Evento Prueba");
        evento.setDescripcion("Descripción");
        evento.setEstado(EstadoEvento.ACTIVO);
        evento.setTipo(TipoEvento.ADULTO);
        evento.setPrecio(20000f);
        evento.setImagenPortada("img.jpg");
        evento = eventoRepository.save(evento);
        assertNotNull(evento.getId());

        // Crear una orden de prueba con el evento
        DetalleOrden detalle = new DetalleOrden(new ObjectId(evento.getId()), 2, LocalDate.now());
        orden = new Orden();
        orden.setIdCliente(new ObjectId(cuenta.getId()));
        orden.setCodigoCupon("DESC10");
        orden.setCodigoPasarela("MP-1234");
        orden.setFecha(LocalDate.now());
        orden.setDetalle(List.of(detalle));
        orden.setTotal(evento.getPrecio() * 2);
        orden = ordenRepository.save(orden);

        // Crear carrito también
        DetalleCarrito detalleCarrito = new DetalleCarrito(evento.getId(), 2, LocalDate.now());
        Carrito carrito = new Carrito();
        carrito.setIdUsuario(cuenta.getId());
        carrito.setItems(List.of(detalleCarrito));
        carritoRepository.save(carrito);
    }
    @Test
    void realizarPago_idOrdenInexistente_lanzaExcepcion() {
        String idInvalido = new ObjectId().toString();

        Exception ex = assertThrows(OrdenException.class, () ->
                ordenService.realizarPago(idInvalido));

        assertEquals("No se encontró la orden con el ID: " + idInvalido, ex.getMessage());
    }

    @Test
    void eliminarOrden_existente_eliminaCorrectamente() {
        assertDoesNotThrow(() -> ordenService.eliminarOrden(orden.getId()));
        assertFalse(ordenRepository.findById(orden.getId()).isPresent());
    }

    @Test
    void eliminarOrden_idInvalido_lanzaExcepcion() {
        String idInvalido = new ObjectId().toString();

        OrdenException ex = assertThrows(OrdenException.class, () -> ordenService.eliminarOrden(idInvalido));
        assertEquals("No se encontró la orden con el ID: " + idInvalido, ex.getMessage());
    }

    @Test
    void actualizarOrden_exitosamente() {
        DetalleOrdenDTO nuevoDetalle = new DetalleOrdenDTO(evento.getId(), 3, LocalDate.now());

        EditarOrdenDTO dto = new EditarOrdenDTO(
                cuenta.getId(),
                null,
                List.of(nuevoDetalle)
        );

        assertDoesNotThrow(() -> ordenService.actualizarOrden(orden.getId(), dto));

        Orden actualizada = ordenRepository.findById(orden.getId()).orElseThrow();
        assertEquals(3, actualizada.getDetalle().get(0).getCantidad());
    }

    @Test
    void actualizarOrden_idInvalido_lanzaExcepcion() {
        DetalleOrdenDTO detalle = new DetalleOrdenDTO(evento.getId(), 2, LocalDate.now());

        EditarOrdenDTO dto = new EditarOrdenDTO(
                cuenta.getId(),
                null,
                List.of(detalle)
        );

        OrdenException ex = assertThrows(OrdenException.class, () ->
                ordenService.actualizarOrden("invalido-id", dto));
        assertEquals("No se encontró la orden con el ID: " + "invalido-id", ex.getMessage());
    }

    @Test
    void actualizarOrden_detalleVacio_lanzaExcepcion() {
        EditarOrdenDTO dto = new EditarOrdenDTO(
                cuenta.getId(),
                null,
                List.of() // vacío
        );

        assertThrows(ConstraintViolationException.class, () ->
                ordenService.actualizarOrden(orden.getId(), dto));
    }

    @Test
    void mostrarOrden_existente_retornaDatosCorrectos() {
        MostrarOrdenDTO dto = assertDoesNotThrow(() -> ordenService.mostrarOrden(orden.getId()));

        assertNotNull(dto);
        assertEquals(cuenta.getNombre(), dto.nombreUsuario());
        assertEquals(orden.getFecha(), dto.fechaCompra());
        assertEquals(orden.getCodigoCupon(), dto.cupon());
        assertEquals(orden.getTotal(), dto.total());
        assertEquals(1, dto.detalles().size());

        MostrarDetalleOrdenDTO detalle = dto.detalles().get(0);
        assertEquals(evento.getId(), detalle.idEvento());
        assertEquals(evento.getNombre(), detalle.nombreEvento());
        assertEquals(evento.getTipo(), detalle.tipoEvento());
        assertEquals(evento.getPrecio(), detalle.precio());
        assertEquals(2, detalle.cantidad());
    }
    @Test
    void mostrarOrden_lanzaExcepcionSiNoExiste() {
        String idInvalido = new ObjectId().toHexString(); // ID no registrado

        Exception ex = assertThrows(OrdenException.class, () -> {
            ordenService.mostrarOrden(idInvalido);
        });

        assertEquals("No se encontró la orden con el ID: " + idInvalido, ex.getMessage());
    }

    @Test
    void crearOrdenDesdeCarrito_conCuponValido_aplicaDescuento() {
        Cupon cupon = new Cupon();
        cupon.setNombre("Cupon de prueba");
        cupon.setCodigo("PRUEBA21");
        cupon.setDescuento(10f);
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setFechaApertura(LocalDateTime.now().minusDays(1));
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(5));
        cuponRepository.save(cupon);

        assertDoesNotThrow(() -> ordenService.crearOrdenDesdeCarrito(cuenta.getId(), "PRUEBA21", "MP-004"));
    }

    @Test
    void crearOrdenDesdeCarrito_exitosamente() {
        assertDoesNotThrow(() -> ordenService.crearOrdenDesdeCarrito(cuenta.getId(), null, "MP-001"));
    }

    @Test
    void crearOrdenDesdeCarrito_carritoVacio_lanzaExcepcion() {
        carritoRepository.deleteAll();
        Carrito carrito = new Carrito();
        carrito.setIdUsuario(cuenta.getId());
        carrito.setItems(new ArrayList<>());

        carritoRepository.save(carrito); // el ID se genera automáticamente aquí

        Exception ex = assertThrows(OrdenException.class,
                () -> ordenService.crearOrdenDesdeCarrito(cuenta.getId(), null, "MP-002"));

        assertTrue(ex.getMessage().toLowerCase().contains("carrito"));
    }

    @Test
    void crearOrdenDesdeCarrito_fechaPasada_lanzaExcepcion() {
        carritoRepository.deleteAll();
        Carrito carrito = new Carrito();
        carrito.setIdUsuario(cuenta.getId());
        carrito.setItems(List.of(
                new DetalleCarrito(evento.getId(), 2, LocalDate.now().minusDays(1)) // fecha pasada
        ));
        carritoRepository.save(carrito); // el ID se genera automáticamente aquí

        Exception ex = assertThrows(OrdenException.class,
                () -> ordenService.crearOrdenDesdeCarrito(cuenta.getId(), null, "MP-003"));

        assertTrue(ex.getMessage().toLowerCase().contains("fecha"));
    }

    @Test
    void crearOrdenDesdeCarrito_cuentaSinCarrito_lanzaExcepcion() {
        cuentaRepository.deleteAll(); // Simula un usuario sin datos relacionados
        Exception ex = assertThrows(CarritoException.class,
                () -> ordenService.crearOrdenDesdeCarrito("0000000000", null, "MP-005"));

        assertTrue(ex.getMessage().toLowerCase().contains("carrito"));
    }

}