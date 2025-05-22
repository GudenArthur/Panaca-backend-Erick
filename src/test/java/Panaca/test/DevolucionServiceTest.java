package Panaca.test;

import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.model.documents.*;
import Panaca.model.enums.*;
import Panaca.model.vo.DetalleOrden;
import Panaca.repository.*;
import Panaca.service.service.DevolucionService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DevolucionServiceTest {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private DevolucionRequestRepository devolucionRequestRepository;

    @Autowired
    private DevolucionService devolucionService;

    private Cuenta cuenta;
    private Orden orden;
    private Donation donation;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setNombre("Usuario Test");
        cuenta.setEmail("devolucion@test.com");
        cuenta.setTelefono("3000000000");
        cuenta.setPassword("segura123");
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta = cuentaRepository.save(cuenta);

        orden = new Orden();
        orden.setIdCliente(new ObjectId(cuenta.getId()));
        orden.setFecha(LocalDate.now().plusWeeks(2)); // fecha futura para permitir devolución
        orden.setDetalle(List.of(new DetalleOrden(new ObjectId(), 1, LocalDate.now().plusWeeks(2))));
        orden.setTotal(50000);
        orden = ordenRepository.save(orden);

        donation = new Donation();
        donation.setIdDonante(cuenta.getId());
        donation.setFecha(LocalDateTime.now().minusHours(1)); // dentro de tiempo válido
        donation.setTotal(100000);
        donation.setItems(List.of(new DonationItem(null, null, AnimalType.PERRO, 1, 100000, 100000)));
        donation = donationRepository.save(donation);
    }

    @Test
    void solicitar_ticketValido_creaSolicitud() {
        DevolucionRequestDTO dto = new DevolucionRequestDTO(
                cuenta.getId(),
                TipoDevolucion.TICKET,
                orden.getId()
        );

        DevolucionResponseDTO respuesta = assertDoesNotThrow(() -> devolucionService.solicitar(dto));

        assertNotNull(respuesta);
        assertEquals(TipoDevolucion.TICKET, respuesta.tipo());
        assertEquals(EstadoDevolucion.PENDIENTE, respuesta.estado());
    }

    @Test
    void solicitar_ticketFueraDeTiempo_lanzaExcepcion() {
        orden.setDetalle(List.of(new DetalleOrden(new ObjectId(), 1, LocalDate.now().plusDays(1))));
        orden = ordenRepository.save(orden);

        DevolucionRequestDTO dto = new DevolucionRequestDTO(
                cuenta.getId(),
                TipoDevolucion.TICKET,
                orden.getId()
        );

        Exception ex = assertThrows(RuntimeException.class, () -> devolucionService.solicitar(dto));
        assertTrue(ex.getMessage().contains("Ya no es posible"));
    }

    @Test
    void solicitar_donacionValida_creaSolicitud() {
        DevolucionRequestDTO dto = new DevolucionRequestDTO(
                cuenta.getId(),
                TipoDevolucion.DONACION,
                donation.getId()
        );

        DevolucionResponseDTO respuesta = assertDoesNotThrow(() -> devolucionService.solicitar(dto));

        assertNotNull(respuesta);
        assertEquals(TipoDevolucion.DONACION, respuesta.tipo());
        assertEquals(EstadoDevolucion.PENDIENTE, respuesta.estado());
    }

    @Test
    void solicitar_donacionPasada24h_lanzaExcepcion() {
        donation.setFecha(LocalDateTime.now().minusHours(30));
        donation = donationRepository.save(donation);

        DevolucionRequestDTO dto = new DevolucionRequestDTO(
                cuenta.getId(),
                TipoDevolucion.DONACION,
                donation.getId()
        );

        Exception ex = assertThrows(RuntimeException.class, () -> devolucionService.solicitar(dto));
        assertTrue(ex.getMessage().contains("Pasaron más de 24h"));
    }

    @Test
    void solicitar_solicitudDuplicada_lanzaExcepcion() {
        DevolucionRequestDTO dto = new DevolucionRequestDTO(
                cuenta.getId(),
                TipoDevolucion.TICKET,
                orden.getId()
        );

        devolucionService.solicitar(dto); // primera vez

        Exception ex = assertThrows(RuntimeException.class, () -> devolucionService.solicitar(dto));
        assertTrue(ex.getMessage().contains("Ya existe una solicitud pendiente"));
    }

    @Test
    void aprobar_solicitudPendiente_cambiaEstado() {
        DevolucionRequest request = new DevolucionRequest(null, cuenta.getId(), TipoDevolucion.TICKET, orden.getId(), LocalDateTime.now(), EstadoDevolucion.PENDIENTE);
        request = devolucionRequestRepository.save(request);

        DevolucionResponseDTO resultado = devolucionService.aprobar(request.getId());

        assertEquals(EstadoDevolucion.APROBADA, resultado.estado());
    }

    @Test
    void rechazar_solicitudPendiente_cambiaEstado() {
        DevolucionRequest request = new DevolucionRequest(null, cuenta.getId(), TipoDevolucion.TICKET, orden.getId(), LocalDateTime.now(), EstadoDevolucion.PENDIENTE);
        request = devolucionRequestRepository.save(request);

        DevolucionResponseDTO resultado = devolucionService.rechazar(request.getId());

        assertEquals(EstadoDevolucion.RECHAZADA, resultado.estado());
    }

    @Test
    void aprobar_solicitudYaResuelta_lanzaExcepcion() {
        DevolucionRequest request = new DevolucionRequest(null, cuenta.getId(), TipoDevolucion.TICKET, orden.getId(), LocalDateTime.now(), EstadoDevolucion.APROBADA);
        request = devolucionRequestRepository.save(request);
        final String requestId = request.getId();

        Exception ex = assertThrows(RuntimeException.class, () -> devolucionService.aprobar(requestId));
        assertTrue(ex.getMessage().contains("ya fue procesada"));
    }

    @Test
    void listarPorUsuario_retornaResultadosCorrectos() {
        devolucionRequestRepository.save(new DevolucionRequest(null, cuenta.getId(), TipoDevolucion.TICKET, orden.getId(), LocalDateTime.now(), EstadoDevolucion.PENDIENTE));
        List<DevolucionResponseDTO> lista = devolucionService.listarPorUsuario(cuenta.getId());

        assertEquals(1, lista.size());
        assertEquals(cuenta.getId(), devolucionRequestRepository.findById(lista.get(0).id()).get().getIdCuenta());
    }

    @Test
    void listarTodas_retornaListaCompleta() {
        devolucionRequestRepository.save(new DevolucionRequest(null, cuenta.getId(), TipoDevolucion.TICKET, orden.getId(), LocalDateTime.now(), EstadoDevolucion.PENDIENTE));
        devolucionRequestRepository.save(new DevolucionRequest(null, cuenta.getId(), TipoDevolucion.DONACION, donation.getId(), LocalDateTime.now(), EstadoDevolucion.PENDIENTE));

        List<DevolucionResponseDTO> lista = devolucionService.listarTodas();
        assertTrue(lista.size() >= 2);
    }

}
