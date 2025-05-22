package Panaca.test;

import Panaca.dto.PQR.*;
import Panaca.exceptions.PQRException;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.PQR;
import Panaca.model.enums.CategoriaPQR;
import Panaca.model.enums.EstadoCuenta;
import Panaca.model.enums.EstadoPQR;
import Panaca.model.enums.Rol;
import Panaca.repository.CuentaRepository;
import Panaca.repository.PQRRepository;
import Panaca.service.service.PQRService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PQRServiceTest {

    @Autowired
    private PQRService pqrService;

    @Autowired
    private PQRRepository pqrRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    private String idUsuario;

    @BeforeEach
    public void setup() {
        Cuenta cuenta = new Cuenta();
        cuenta.setCedula("123456789");
        cuenta.setNombre("Test User");
        cuenta.setTelefono("3001234567");
        cuenta.setEmail(UUID.randomUUID() + "@test.com");
        cuenta.setPassword("password");
        cuenta.setRol(Rol.CLIENTE);
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        idUsuario = cuentaRepository.save(cuenta).getId();
    }

    @Test
    public void crearPQR_exitosamente() {
        CrearPQRDTO dto = new CrearPQRDTO(idUsuario, CategoriaPQR.RECLAMO, "Producto en mal estado.");

        assertDoesNotThrow(() -> pqrService.crearPQR(dto));

        List<PQR> resultados = pqrRepository.findByIdUsuario(idUsuario);
        assertFalse(resultados.isEmpty());
        assertEquals(CategoriaPQR.RECLAMO, resultados.get(0).getCategoriaPQR());
    }

    @Test
    public void eliminarPQR_exitosamente() throws PQRException {
        PQR pqr = new PQR();
        pqr.setIdUsuario(idUsuario);
        pqr.setDescripcion("Problema de facturación");
        pqr.setEstadoPQR(EstadoPQR.PENDIENTE);
        pqr.setCategoriaPQR(CategoriaPQR.FACTURACION);
        pqr.setFechaCreacion(LocalDateTime.now());
        pqr = pqrRepository.save(pqr);

        pqrService.eliminarPQR(pqr.getId());

        PQR actualizado = pqrRepository.findById(pqr.getId()).orElseThrow();
        assertEquals(EstadoPQR.ELIMINADO, actualizado.getEstadoPQR());
    }

    @Test
    public void obtenerInformacionPQR_exitosamente() throws PQRException {
        PQR pqr = new PQR();
        pqr.setIdUsuario(idUsuario);
        pqr.setDescripcion("Solicito información del evento.");
        pqr.setEstadoPQR(EstadoPQR.PENDIENTE);
        pqr.setCategoriaPQR(CategoriaPQR.SERVICIO_CLIENTE);
        pqr.setFechaCreacion(LocalDateTime.now());
        pqr = pqrRepository.save(pqr);

        InformacionPQRDTO info = pqrService.obtenerInformacionPQR(pqr.getId());

        assertEquals(pqr.getId(), info.id());
        assertEquals("Test User", info.nombreUsuario());
    }

    @Test
    public void responderPQR_exitosamente() throws PQRException {
        PQR pqr = new PQR();
        pqr.setIdUsuario(idUsuario);
        pqr.setDescripcion("No aplicaron mi cupón.");
        pqr.setEstadoPQR(EstadoPQR.PENDIENTE);
        pqr.setCategoriaPQR(CategoriaPQR.CUPON);
        pqr.setFechaCreacion(LocalDateTime.now());
        pqr = pqrRepository.save(pqr);

        ResponderPQRDTO dto = new ResponderPQRDTO(pqr.getId(), "El cupón ya no estaba disponible.");
        pqrService.responderPQR(dto);

        PQR actualizado = pqrRepository.findById(pqr.getId()).orElseThrow();
        assertEquals(EstadoPQR.RESUELTO, actualizado.getEstadoPQR());
        assertNotNull(actualizado.getFechaRespuesta());
    }

    @Test
    public void obtenerPQRsPorUsuario_conResultados() throws PQRException {
        PQR pqr = new PQR();
        pqr.setIdUsuario(idUsuario);
        pqr.setDescripcion("Evento cancelado sin aviso.");
        pqr.setEstadoPQR(EstadoPQR.PENDIENTE);
        pqr.setCategoriaPQR(CategoriaPQR.RECLAMO);
        pqr.setFechaCreacion(LocalDateTime.now());
        pqrRepository.save(pqr);

        List<PQR> resultados = pqrService.obtenerPQRsPorUsuario(idUsuario);
        assertFalse(resultados.isEmpty());
    }

    @Test
    public void listarPQRs_exitosamente() throws PQRException {
        PQR pqr = new PQR();
        pqr.setIdUsuario(idUsuario);
        pqr.setDescripcion("Consulta general.");
        pqr.setEstadoPQR(EstadoPQR.PENDIENTE);
        pqr.setCategoriaPQR(CategoriaPQR.OTROS);
        pqr.setFechaCreacion(LocalDateTime.now());
        pqrRepository.save(pqr);

        List<ItemPQRDTO> lista = pqrService.listarPQRs();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void responderPQR_yaResuelta_debeLanzarExcepcion() {
        PQR pqr = new PQR();
        pqr.setIdUsuario(idUsuario);
        pqr.setDescripcion("Ya respondida.");
        pqr.setEstadoPQR(EstadoPQR.RESUELTO);
        pqr.setCategoriaPQR(CategoriaPQR.SERVICIO_CLIENTE);
        pqr.setFechaCreacion(LocalDateTime.now());
        pqr = pqrRepository.save(pqr);

        ResponderPQRDTO dto = new ResponderPQRDTO(pqr.getId(), "Respuesta duplicada");

        Exception ex = assertThrows(PQRException.class, () -> pqrService.responderPQR(dto));
        assertTrue(ex.getMessage().contains("ya ha sido resuelta"));
    }

    @Test
    public void obtenerPQRsPorUsuario_sinResultados_debeLanzarExcepcion() {
        String idFalso = "no-existe";

        Exception ex = assertThrows(PQRException.class, () -> pqrService.obtenerPQRsPorUsuario(idFalso));
        assertTrue(ex.getMessage().contains("No se encontraron"));
    }

    @Test
    public void listarPQRs_sinRegistros_debeLanzarExcepcion() {
        pqrRepository.deleteAll();
        Exception ex = assertThrows(PQRException.class, () -> pqrService.listarPQRs());
        assertTrue(ex.getMessage().contains("No hay PQRs"));
    }
}