package Panaca.test;

import Panaca.dto.carrito.CrearCarritoDTO;
import Panaca.dto.carrito.DetalleCarritoDTO;
import Panaca.dto.carrito.InformacionEventoCarritoDTO;
import Panaca.exceptions.CarritoException;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Evento;
import Panaca.model.enums.EstadoEvento;
import Panaca.model.enums.TipoEvento;
import Panaca.model.vo.DetalleCarrito;
import Panaca.repository.CarritoRepository;
import Panaca.repository.EventoRepository;
import Panaca.service.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CarritoServiceTest {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Test
    public void crearCarrito_exitosamente() {
        String idUsuario = UUID.randomUUID().toString();

        CrearCarritoDTO dto = new CrearCarritoDTO(idUsuario, List.of());

        assertDoesNotThrow(() -> carritoService.crearCarrito(dto));

        Optional<Carrito> carritoOpt = carritoRepository.findByIdUsuario(idUsuario);
        assertTrue(carritoOpt.isPresent());

        Carrito carrito = carritoOpt.get();
        assertEquals(idUsuario, carrito.getIdUsuario());
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    public void obtenerCarritoPorUsuario_idValido_devuelveCarrito() throws CarritoException {
        String idUsuario = UUID.randomUUID().toString();
        Carrito carrito = new Carrito(null, idUsuario, new ArrayList<>());
        carritoRepository.save(carrito);

        Carrito obtenido = carritoService.obtenerCarritoPorUsuario(idUsuario);

        assertNotNull(obtenido);
        assertEquals(idUsuario, obtenido.getIdUsuario());
    }

    @Test
    public void obtenerCarritoPorUsuario_idInvalido_lanzaExcepcion() {
        String idFalso = "000000000000000000000000";

        Exception ex = assertThrows(CarritoException.class, () ->
                carritoService.obtenerCarritoPorUsuario(idFalso));

        assertTrue(ex.getMessage().toLowerCase().contains("carrito"));
    }

    @Test
    public void agregarItemsAlCarrito_itemsValidos_actualizaCantidad() throws CarritoException {
        String idUsuario = UUID.randomUUID().toString();
        Carrito carrito = new Carrito(null, idUsuario, new ArrayList<>());
        carritoRepository.save(carrito);

        DetalleCarritoDTO itemDTO = new DetalleCarritoDTO("evento123", 2, LocalDate.now());

        Carrito actualizado = carritoService.agregarItemsAlCarrito(idUsuario, List.of(itemDTO));

        assertEquals(1, actualizado.getItems().size());
        DetalleCarrito item = actualizado.getItems().get(0);
        assertEquals("evento123", item.getIdEvento());
        assertEquals(2, item.getCantidad());
    }

    @Test
    public void agregarItemsAlCarrito_listaVacia_lanzaExcepcion() {
        String idUsuario = UUID.randomUUID().toString();
        Carrito carrito = new Carrito(null, idUsuario, new ArrayList<>());
        carritoRepository.save(carrito);

        Exception ex = assertThrows(CarritoException.class, () ->
                carritoService.agregarItemsAlCarrito(idUsuario, List.of()));

        assertTrue(ex.getMessage().toLowerCase().contains("ítems"));
    }

    @Test
    public void agregarItemsAlCarrito_itemsDuplicados_incrementaCantidad() throws CarritoException {
        String idUsuario = UUID.randomUUID().toString();
        DetalleCarrito existente = new DetalleCarrito("evento123", 1, LocalDate.now());

        Carrito carrito = new Carrito(null, idUsuario, new ArrayList<>(List.of(existente)));
        carritoRepository.save(carrito);

        DetalleCarritoDTO nuevoItemDTO = new DetalleCarritoDTO("evento123", 3, existente.getFechaUso());

        Carrito actualizado = carritoService.agregarItemsAlCarrito(idUsuario, List.of(nuevoItemDTO));

        assertEquals(1, actualizado.getItems().size());
        assertEquals(4, actualizado.getItems().get(0).getCantidad());
    }

    @Test
    public void eliminarItemDelCarrito_itemExistente_eliminaCorrectamente() throws CarritoException {
        String idUsuario = UUID.randomUUID().toString();
        String idEvento = "evento123";
        LocalDate fechaUso = LocalDate.now();

        DetalleCarrito item = new DetalleCarrito(idEvento, 2, fechaUso);
        Carrito carrito = new Carrito(null, idUsuario, new ArrayList<>(List.of(item)));
        carritoRepository.save(carrito);

        Carrito actualizado = carritoService.eliminarItemDelCarrito(idUsuario, idEvento, fechaUso);

        assertTrue(actualizado.getItems().isEmpty());
    }

    @Test
    public void eliminarItemDelCarrito_itemNoExiste_lanzaExcepcion() {
        String idUsuario = UUID.randomUUID().toString();
        LocalDate fechaUso = LocalDate.now();

        DetalleCarrito item = new DetalleCarrito("eventoReal", 2, fechaUso);
        Carrito carrito = new Carrito(null, idUsuario, new ArrayList<>(List.of(item)));
        carritoRepository.save(carrito);

        Exception ex = assertThrows(CarritoException.class, () ->
                carritoService.eliminarItemDelCarrito(idUsuario, "eventoFalso", fechaUso));

        assertTrue(ex.getMessage().toLowerCase().contains("no se encontró"));
    }

    @Test
    public void vaciarCarrito_conItems_vaciaCorrectamente() throws CarritoException {
        String idUsuario = UUID.randomUUID().toString();
        List<DetalleCarrito> items = List.of(
                new DetalleCarrito("evento1", 2, LocalDate.now()),
                new DetalleCarrito("evento2", 1, LocalDate.now())
        );
        carritoRepository.save(new Carrito(null, idUsuario, new ArrayList<>(items)));

        Carrito vacio = carritoService.vaciarCarrito(idUsuario);

        assertTrue(vacio.getItems().isEmpty());
    }

    @Test
    public void vaciarCarrito_sinCarrito_lanzaExcepcionTest() {
        String idInexistente = "no-existe";

        Exception ex = assertThrows(CarritoException.class, () -> carritoService.vaciarCarrito(idInexistente));
        assertTrue(ex.getMessage().toLowerCase().contains("no se encontró"));
    }

    @Test
    public void listarProductosEnCarrito_conEventosExistentes_retornaInformacion() throws Exception {
        String idUsuario = UUID.randomUUID().toString();
        String idEvento = UUID.randomUUID().toString();

        Evento evento = new Evento();
        evento.setId(idEvento);
        evento.setNombre("Evento Test");
        evento.setImagenPortada("imagen.jpg");
        evento.setDescripcion("desc");
        evento.setEstado(EstadoEvento.ACTIVO);
        evento.setTipo(TipoEvento.ADULTO);
        evento.setPrecio(123.45f);
        eventoRepository.save(evento);

        DetalleCarrito item = new DetalleCarrito(idEvento, 2, LocalDate.now());
        carritoRepository.save(new Carrito(null, idUsuario, List.of(item)));

        List<InformacionEventoCarritoDTO> resultados = carritoService.listarProductosEnCarrito(idUsuario);

        assertEquals(1, resultados.size());
        InformacionEventoCarritoDTO resultado = resultados.get(0);
        assertEquals("Evento Test", resultado.nombre());
        assertEquals("imagen.jpg", resultado.imagenPortada());
        assertEquals(idEvento, resultado.detalleCarritoDTO().idEvento());
        assertEquals(2, resultado.detalleCarritoDTO().cantidad());
    }

    @Test
    public void listarProductosEnCarrito_eventoNoExiste_lanzaExcepcion() {
        String idUsuario = UUID.randomUUID().toString();
        String idEventoInexistente = UUID.randomUUID().toString();

        carritoRepository.save(new Carrito(null, idUsuario, List.of(
                new DetalleCarrito(idEventoInexistente, 1, LocalDate.now())
        )));

        Exception ex = assertThrows(CarritoException.class, () -> carritoService.listarProductosEnCarrito(idUsuario));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
    }

    @Test
    public void calcularTotalCarrito_conEventosValidos_retornaSumaCorrecta() throws Exception {
        String idUsuario = UUID.randomUUID().toString();
        String idEvento1 = UUID.randomUUID().toString();
        String idEvento2 = UUID.randomUUID().toString();

        Evento evento1 = new Evento(idEvento1, "Evento 1", "", "", EstadoEvento.ACTIVO, TipoEvento.ADULTO, 100f);
        Evento evento2 = new Evento(idEvento2, "Evento 2", "", "", EstadoEvento.ACTIVO, TipoEvento.NINO, 150f);

        eventoRepository.saveAll(List.of(evento1, evento2));

        List<DetalleCarrito> items = List.of(
                new DetalleCarrito(idEvento1, 2, LocalDate.now()),
                new DetalleCarrito(idEvento2, 1, LocalDate.now())
        );

        carritoRepository.save(new Carrito(null, idUsuario, items));

        double total = carritoService.calcularTotalCarrito(idUsuario);

        assertEquals(350.0, total);
    }

    @Test
    public void calcularTotalCarrito_eventoInexistente_lanzaExcepcion() {
        String idUsuario = UUID.randomUUID().toString();
        String idFalso = UUID.randomUUID().toString();

        DetalleCarrito item = new DetalleCarrito(idFalso, 1, LocalDate.now());
        carritoRepository.save(new Carrito(null, idUsuario, List.of(item)));

        Exception ex = assertThrows(RuntimeException.class, () -> carritoService.calcularTotalCarrito(idUsuario));
        assertTrue(ex.getMessage().toLowerCase().contains("evento no encontrado"));
    }
}