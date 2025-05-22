package Panaca.test;

import Panaca.dto.evento.CrearEventoDTO;
import Panaca.dto.evento.EditarEventoDTO;
import Panaca.dto.evento.EventoFiltradoDTO;
import Panaca.dto.evento.ItemEventoDTO;
import Panaca.exceptions.EventoException;
import Panaca.model.enums.*;
import Panaca.model.documents.Evento;
import Panaca.repository.EventoRepository;
import Panaca.service.service.EventoService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EventoServiceTest {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private EventoRepository eventoRepository;

    private Evento eventoExistente;
    private Evento evento1;
    private Evento evento2;

    @BeforeEach
    void setUp() {
        eventoRepository.deleteAll();

        eventoExistente = new Evento();
        eventoExistente.setNombre("Evento Original");
        eventoExistente.setDescripcion("Descripcion original");
        eventoExistente.setImagenPortada("imagen.jpg");
        eventoExistente.setTipo(TipoEvento.ADULTO);
        eventoExistente.setEstado(EstadoEvento.ACTIVO);
        eventoExistente.setPrecio(15000f);
        eventoRepository.save(eventoExistente);

        evento1 = new Evento();
        evento1.setNombre("Festival Cultural");
        evento1.setDescripcion("Evento artístico");
        evento1.setImagenPortada("img1.jpg");
        evento1.setTipo(TipoEvento.ADULTO);
        evento1.setEstado(EstadoEvento.ACTIVO);
        evento1.setPrecio(10000f);

        evento2 = new Evento();
        evento2.setNombre("Concierto Infantil");
        evento2.setDescripcion("Música para niños");
        evento2.setImagenPortada("img2.jpg");
        evento2.setTipo(TipoEvento.NINO);
        evento2.setEstado(EstadoEvento.INACTIVO);
        evento2.setPrecio(8000f);

        eventoRepository.saveAll(List.of(evento1, evento2));
    }

    @Test
    public void crearEvento_exitosamente() {
        CrearEventoDTO dto = new CrearEventoDTO(
                "Festival Musical",
                "Un evento musical para toda la familia",
                "imagen.jpg",
                EstadoEvento.ACTIVO,
                TipoEvento.ADULTO,
                50000f
        );

        assertDoesNotThrow(() -> eventoService.crearEvento(dto));

        List<Evento> eventos = eventoRepository.findByNombre("Festival Musical");
        assertFalse(eventos.isEmpty());
        Evento evento = eventos.get(0);
        assertEquals("Festival Musical", evento.getNombre());
    }

    @Test
    public void crearEvento_conNombreVacio_lanzaExcepcion() {
        CrearEventoDTO dto = new CrearEventoDTO(
                "", // inválido
                "Descripción válida",
                "imagen.jpg",
                EstadoEvento.ACTIVO,
                TipoEvento.NINO,
                25000f
        );

        assertThrows(ConstraintViolationException.class, () -> eventoService.crearEvento(dto));
    }

    @Test
    public void crearEvento_conPrecioInvalido_lanzaExcepcion() {
        CrearEventoDTO dto = new CrearEventoDTO(
                "Evento Precio Inválido",
                "Una descripción válida",
                "imagen.jpg",
                EstadoEvento.INACTIVO,
                TipoEvento.ADULTO,
                0f // inválido
        );

        assertThrows(ConstraintViolationException.class, () -> eventoService.crearEvento(dto));
    }

    @Test
    void editarEvento_exitosamente() {
        EditarEventoDTO dto = new EditarEventoDTO(
                "Nombre Actualizado",
                "Descripcion actualizada del evento",
                "nueva_imagen.jpg",
                EstadoEvento.INACTIVO,
                TipoEvento.NINO,
                20000f
        );

        assertDoesNotThrow(() -> eventoService.editarEvento(eventoExistente.getId(), dto));

        Evento actualizado = eventoRepository.findById(eventoExistente.getId()).orElseThrow();
        assertEquals("Nombre Actualizado", actualizado.getNombre());
        assertEquals("Descripcion actualizada del evento", actualizado.getDescripcion());
        assertEquals("nueva_imagen.jpg", actualizado.getImagenPortada());
        assertEquals(EstadoEvento.INACTIVO, actualizado.getEstado());
        assertEquals(TipoEvento.NINO, actualizado.getTipo());
        assertEquals(20000f, actualizado.getPrecio());
    }

    @Test
    void editarEvento_idInexistente_lanzaExcepcion() {
        EditarEventoDTO dto = new EditarEventoDTO(
                "Nombre",
                "Descripcion",
                "imagen.jpg",
                EstadoEvento.ACTIVO,
                TipoEvento.ADULTO,
                15000f
        );

        EventoException ex = assertThrows(EventoException.class, () ->
                eventoService.editarEvento("000000000000000000000000", dto)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("no existe"));
    }

    @Test
    void editarEvento_nombreVacio_lanzaExcepcion() {
        EditarEventoDTO dto = new EditarEventoDTO(
                "", // nombre vacío
                "Descripcion",
                "imagen.jpg",
                EstadoEvento.ACTIVO,
                TipoEvento.ADULTO,
                15000f
        );

        assertThrows(ConstraintViolationException.class, () ->
                eventoService.editarEvento(eventoExistente.getId(), dto)
        );
    }

    @Test
    void editarEvento_precioInvalido_lanzaExcepcion() {
        EditarEventoDTO dto = new EditarEventoDTO(
                "Evento Prueba",
                "Descripcion",
                "imagen.jpg",
                EstadoEvento.ACTIVO,
                TipoEvento.ADULTO,
                0f // precio inválido
        );

        assertThrows(ConstraintViolationException.class, () ->
                eventoService.editarEvento(eventoExistente.getId(), dto)
        );
    }

    @Test
    void eliminarEvento_exitosamente() {
        String idEvento = eventoExistente.getId();

        assertDoesNotThrow(() -> eventoService.eliminarEvento(idEvento));

        Evento eventoEliminado = eventoRepository.findById(idEvento).orElseThrow();
        assertEquals(EstadoEvento.ELIMINADO, eventoEliminado.getEstado());
    }

    @Test
    void eliminarEvento_idInvalido_lanzaExcepcion() {
        String idInexistente = "000000000000000000000000";

        EventoException exception = assertThrows(EventoException.class,
                () -> eventoService.eliminarEvento(idInexistente));

        assertTrue(exception.getMessage().toLowerCase().contains("evento"));
    }

    @Test
    void obtenerInformacionEvento_idValido_retornaEvento() throws EventoException {
        Evento obtenido = assertDoesNotThrow(() ->
                eventoService.obtenerInformacionEvento(eventoExistente.getId()));

        assertEquals(eventoExistente.getId(), obtenido.getId());
        assertEquals("Evento Original", obtenido.getNombre());
        assertEquals("Descripcion original", obtenido.getDescripcion());
    }

    @Test
    void obtenerInformacionEvento_idInvalido_lanzaExcepcion() {
        String idFalso = "000000000000000000000000";

        EventoException exception = assertThrows(EventoException.class, () ->
                eventoService.obtenerInformacionEvento(idFalso));

        assertTrue(exception.getMessage().toLowerCase().contains("evento"));
    }

    @Test
    void listarEventos_eventosDisponibles_retornaListaDTOs() {
        List<ItemEventoDTO> lista = assertDoesNotThrow(() -> eventoService.listarEventos());

        assertNotNull(lista);
        assertFalse(lista.isEmpty());

        ItemEventoDTO eventoDTO = lista.stream()
                .filter(e -> e.id().equals(eventoExistente.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(eventoDTO);
        assertEquals(eventoExistente.getId(), eventoDTO.id());
        assertEquals(eventoExistente.getNombre(), eventoDTO.nombre());
        assertEquals(eventoExistente.getDescripcion(), eventoDTO.descripcion());
        assertEquals(eventoExistente.getImagenPortada(), eventoDTO.imagenPortada());
        assertEquals(eventoExistente.getTipo(), eventoDTO.tipoEvento());
        assertEquals(eventoExistente.getPrecio(), eventoDTO.precio());
    }

    @Test
    void filtrarEventos_porNombre_retornaCoincidencia() {
        EventoFiltradoDTO filtro = new EventoFiltradoDTO("Festival", null, null);

        List<EventoFiltradoDTO> resultados = eventoService.filtrarEventos(filtro);
        assertEquals(1, resultados.size());
        assertEquals("Festival Cultural", resultados.get(0).nombre());
    }

    @Test
    void filtrarEventos_porTipo_retornaCoincidencia() {
        EventoFiltradoDTO filtro = new EventoFiltradoDTO(null, TipoEvento.NINO, null);

        List<EventoFiltradoDTO> resultados = eventoService.filtrarEventos(filtro);
        assertEquals(1, resultados.size());
        assertEquals(TipoEvento.NINO, resultados.get(0).tipoEvento());
    }

    @Test
    void filtrarEventos_porEstado_retornaCoincidencia() {
        EventoFiltradoDTO filtro = new EventoFiltradoDTO(null, null, EstadoEvento.ACTIVO);

        List<EventoFiltradoDTO> resultados = eventoService.filtrarEventos(filtro);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(e -> e.estadoEvento() == EstadoEvento.ACTIVO));
    }

    @Test
    void filtrarEventos_combinado_retornaExacto() {
        EventoFiltradoDTO filtro = new EventoFiltradoDTO("Concierto", TipoEvento.NINO, EstadoEvento.INACTIVO);

        List<EventoFiltradoDTO> resultados = eventoService.filtrarEventos(filtro);
        assertEquals(1, resultados.size());
        assertEquals("Concierto Infantil", resultados.get(0).nombre());
    }

    @Test
    void filtrarEventos_sinResultados_lanzaExcepcion() {
        EventoFiltradoDTO filtro = new EventoFiltradoDTO("NoExiste", TipoEvento.ADULTO, EstadoEvento.INACTIVO);

        Exception ex = assertThrows(EventoException.class, () -> eventoService.filtrarEventos(filtro));
        assertTrue(ex.getMessage().toLowerCase().contains("no se encontraron eventos"));
    }
}