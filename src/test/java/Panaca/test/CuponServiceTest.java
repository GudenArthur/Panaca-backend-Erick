package Panaca.test;

import Panaca.dto.cupon.*;
import Panaca.exceptions.CuponException;
import Panaca.model.documents.Cupon;
import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import Panaca.repository.CuponRepository;
import Panaca.service.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CuponServiceTest {

    @Autowired
    private CuponService cuponService;

    @Autowired
    private CuponRepository cuponRepository;

    @Test
    public void crearCupon_exitosamente() {
        String codigo = UUID.randomUUID().toString().substring(0, 10);

        CrearCuponDTO dto = new CrearCuponDTO(
                codigo,
                "Nombre Test",
                10.5f,
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now(),
                TipoCupon.UNICO,
                EstadoCupon.DISPONIBLE
        );

        assertDoesNotThrow(() -> cuponService.crearCupon(dto));

        Optional<Cupon> cupon = cuponRepository.findByCodigo(codigo);
        assertTrue(cupon.isPresent());
        assertEquals(codigo, cupon.get().getCodigo());
    }

    @Test
    public void crearCupon_codigoDuplicado_lanzaExcepcion() {
        String codigo = UUID.randomUUID().toString().substring(0, 10);

        CrearCuponDTO dto = new CrearCuponDTO(
                codigo,
                "Nombre Test",
                15f,
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now(),
                TipoCupon.UNICO,
                EstadoCupon.DISPONIBLE
        );

        assertDoesNotThrow(() -> cuponService.crearCupon(dto));

        Exception ex = assertThrows(CuponException.class, () -> cuponService.crearCupon(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("codigo"));
    }

    @Test
    public void editarCupon_exitosamente() {
        Cupon cupon = new Cupon();
        cupon.setCodigo("EDITAR123");
        cupon.setNombre("Editar Nombre");
        cupon.setDescuento(20f);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(5));
        cupon.setFechaApertura(LocalDateTime.now());
        cupon = cuponRepository.save(cupon);

        EditarCuponDTO dto = new EditarCuponDTO(
                "EDITAR123",
                "Nombre Editado",
                25f,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(1),
                TipoCupon.UNICO,
                EstadoCupon.NO_DISPONIBLE
        );

        final String cuponId = cupon.getId();
        assertDoesNotThrow(() -> cuponService.editarCupon(dto, cuponId));

        Cupon actualizado = cuponRepository.findById(cupon.getId()).orElseThrow();
        assertEquals("Nombre Editado", actualizado.getNombre());
        assertEquals(25f, actualizado.getDescuento());
    }

    @Test
    public void editarCupon_idInexistente_lanzaExcepcion() {
        EditarCuponDTO dto = new EditarCuponDTO(
                "XXXX",
                "Nombre Test",
                10f,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                TipoCupon.UNICO,
                EstadoCupon.DISPONIBLE
        );

        Exception ex = assertThrows(CuponException.class, () -> cuponService.editarCupon(dto, "000000000000000000000000"));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrada"));
    }

    @Test
    public void eliminarCupon_exitosamente() {
        Cupon cupon = new Cupon();
        cupon.setCodigo("ELIMINAR123");
        cupon.setNombre("Eliminar Nombre");
        cupon.setDescuento(20f);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(5));
        cupon.setFechaApertura(LocalDateTime.now());
        cupon = cuponRepository.save(cupon);

        final String cuponId = cupon.getId();
        assertDoesNotThrow(() -> cuponService.eliminarCupon(cuponId));
        assertFalse(cuponRepository.findById(cupon.getId()).isPresent());
    }

    @Test
    public void eliminarCupon_idInvalido_lanzaExcepcion() {
        Exception ex = assertThrows(CuponException.class, () -> cuponService.eliminarCupon(""));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    public void obtenerInformacionCupon_exitosamente() throws CuponException {
        Cupon cupon = new Cupon();
        cupon.setNombre("Cupon del amor");
        cupon.setCodigo("AMORRRRR");
        cupon.setDescuento(15f);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(5));
        cupon.setFechaApertura(LocalDateTime.now());
        cupon = cuponRepository.save(cupon);

        final String cuponId = cupon.getId();
        InformacionCuponDTO dto = assertDoesNotThrow(() -> cuponService.obtenerInformacionCupon(cuponId));

        assertEquals(cupon.getCodigo(), dto.codigo());
        assertEquals(cupon.getNombre(), dto.nombre());
        assertEquals(cupon.getDescuento(), dto.porcentajeDescuento());
    }

    @Test
    public void obtenerInformacionCupon_conIdVacio_lanzaExcepcion() {
        Exception ex = assertThrows(CuponException.class, () -> cuponService.obtenerInformacionCupon(""));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    public void obtenerInformacionCupon_idInexistente_lanzaExcepcion() {
        Exception ex = assertThrows(CuponException.class, () -> cuponService.obtenerInformacionCupon("000000000000000000000000"));
        assertTrue(ex.getMessage().toLowerCase().contains("no encontrado"));
    }

    @Test
    public void getAllDisponibles_y_NoDisponibles() {
        Cupon disponible = new Cupon("Cupón A", "CODA", LocalDateTime.now().plusDays(1), LocalDateTime.now(), 10f, TipoCupon.UNICO, EstadoCupon.DISPONIBLE, false);
        Cupon noDisponible = new Cupon("Cupón B", "CODB", LocalDateTime.now().plusDays(1), LocalDateTime.now(), 15f, TipoCupon.UNICO, EstadoCupon.NO_DISPONIBLE, false);

        cuponRepository.save(disponible);
        cuponRepository.save(noDisponible);

        PageRequest pageable = PageRequest.of(0, 10);

        Page<Cupon> disponibles = cuponService.getAllDisponibles(pageable);
        Page<Cupon> noDisponibles = cuponService.getAllNoDisponibles(pageable);

        assertTrue(disponibles.getContent().stream().anyMatch(c -> c.getCodigo().equals("CODA")));
        assertTrue(noDisponibles.getContent().stream().anyMatch(c -> c.getCodigo().equals("CODB")));
    }

    @Test
    public void obtenerCuponesFiltrados_porNombre() {
        Cupon cupon = new Cupon();
        cupon.setCodigo("FILTRO01");
        cupon.setNombre("Descuento Super Especial");
        cupon.setDescuento(15f);
        cupon.setTipo(TipoCupon.UNICO);
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(10));
        cupon.setFechaApertura(LocalDateTime.now());
        cuponRepository.save(cupon);

        ItemsCuponFiltroDTO filtro = new ItemsCuponFiltroDTO("super", null, null, null, null, null);
        List<ItemsCuponDTO> resultados = cuponService.obtenerCuponesFiltrados(filtro);

        assertFalse(resultados.isEmpty());
        assertTrue(resultados.get(0).nombre().toLowerCase().contains("super"));
    }

    @Test
    public void obtenerCuponesFiltrados_porFechas() {
        Cupon cupon = new Cupon();
        cupon.setCodigo("FILTRO02");
        cupon.setNombre("Cupon Fecha");
        cupon.setDescuento(20f);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
        cupon.setFechaApertura(LocalDateTime.now().minusDays(1));
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(5));
        cuponRepository.save(cupon);

        ItemsCuponFiltroDTO filtro = new ItemsCuponFiltroDTO(
                null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(2),
                null,
                null,
                null
        );

        List<ItemsCuponDTO> resultados = cuponService.obtenerCuponesFiltrados(filtro);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.get(0).fechaVencimiento().isAfter(LocalDateTime.now()));
    }

    @Test
    public void obtenerCuponesFiltrados_combinado() {
        String nombreUnico = "UnicoNombre_" + UUID.randomUUID();

        Cupon cupon = new Cupon();
        cupon.setNombre(nombreUnico);
        cupon.setCodigo("COMBINADO");
        cupon.setDescuento(25f);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setFechaApertura(LocalDateTime.now().minusDays(1));
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        cuponRepository.save(cupon);

        ItemsCuponFiltroDTO filtro = new ItemsCuponFiltroDTO(
                nombreUnico,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(2),
                25f,
                TipoCupon.MULTIPLE,
                EstadoCupon.DISPONIBLE
        );

        List<ItemsCuponDTO> resultados = cuponService.obtenerCuponesFiltrados(filtro);
        assertEquals(1, resultados.size());
        assertEquals(nombreUnico, resultados.get(0).nombre());
    }

    @Test
    public void obtenerCuponesFiltrados_sinResultados() {
        ItemsCuponFiltroDTO filtro = new ItemsCuponFiltroDTO(
                "inexistente",
                LocalDateTime.now().plusYears(5),
                LocalDateTime.now().plusYears(5),
                99f,
                TipoCupon.UNICO,
                EstadoCupon.NO_DISPONIBLE
        );

        List<ItemsCuponDTO> resultados = cuponService.obtenerCuponesFiltrados(filtro);
        assertTrue(resultados.isEmpty());
    }

    @Test
    public void aplicarCupon_disponibleNoUsado_devuelveCupon() {
        Cupon cupon = new Cupon();
        cupon.setId("UNICO1");
        cupon.setCodigo("UNICO1");
        cupon.setNombre("Cupón Único");
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setTipo(TipoCupon.UNICO);
        cupon.setDescuento(10f);
        cupon.setFechaApertura(LocalDateTime.now().minusDays(1));
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(2));
        cupon.setUtilizado(false);
        cuponRepository.save(cupon);

        Cupon resultado = assertDoesNotThrow(() -> cuponService.aplicarCupon("UNICO1", LocalDateTime.now()));
        assertNotNull(resultado);
        assertTrue(resultado.isUtilizado());
    }
    @Test
    public void aplicarCupon_noExiste_lanzaExcepcion() {
        Exception ex = assertThrows(CuponException.class, () ->
                cuponService.aplicarCupon("INEXISTENTE", LocalDateTime.now())
        );
        assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
    }
    @Test
    public void aplicarCupon_noDisponible_lanzaExcepcion() {
        Cupon cupon = new Cupon();
        cupon.setId("NO_DISP");
        cupon.setCodigo("NO_DISP");
        cupon.setNombre("No disponible");
        cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setDescuento(10f);
        cupon.setFechaApertura(LocalDateTime.now());
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(5));
        cuponRepository.save(cupon);

        Exception ex = assertThrows(CuponException.class, () ->
                cuponService.aplicarCupon("NO_DISP", LocalDateTime.now())
        );
        assertTrue(ex.getMessage().toLowerCase().contains("disponible"));
    }
    @Test
    public void aplicarCupon_expirado_lanzaExcepcion() {
        Cupon cupon = new Cupon();
        cupon.setId("EXP01");
        cupon.setCodigo("EXP01");
        cupon.setNombre("Cupón Expirado");
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setTipo(TipoCupon.MULTIPLE);
        cupon.setDescuento(5f);
        cupon.setFechaApertura(LocalDateTime.now().minusDays(5));
        cupon.setFechaVencimiento(LocalDateTime.now().minusDays(1));
        cuponRepository.save(cupon);

        Exception ex = assertThrows(CuponException.class, () ->
                cuponService.aplicarCupon("EXP01", LocalDateTime.now())
        );
        assertTrue(ex.getMessage().toLowerCase().contains("expirado"));
    }
    @Test
    public void aplicarCupon_unicoYaUtilizado_lanzaExcepcion() {
        Cupon cupon = new Cupon();
        cupon.setId("USED01");
        cupon.setCodigo("USED01");
        cupon.setNombre("Usado");
        cupon.setEstado(EstadoCupon.DISPONIBLE);
        cupon.setTipo(TipoCupon.UNICO);
        cupon.setUtilizado(true);
        cupon.setDescuento(15f);
        cupon.setFechaApertura(LocalDateTime.now().minusDays(1));
        cupon.setFechaVencimiento(LocalDateTime.now().plusDays(1));
        cuponRepository.save(cupon);

        Exception ex = assertThrows(CuponException.class, () ->
                cuponService.aplicarCupon("USED01", LocalDateTime.now())
        );
        assertTrue(ex.getMessage().toLowerCase().contains("utilizado"));
    }
}