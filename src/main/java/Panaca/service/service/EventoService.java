package Panaca.service.service;

import Panaca.dto.evento.*;
import Panaca.model.documents.Evento;
import Panaca.exceptions.EventoException;
import jakarta.validation.Valid;

import java.util.List;

public interface EventoService {

    void crearEvento(@Valid CrearEventoDTO crearEventoDTO) throws EventoException;

    void editarEvento(String id,@Valid EditarEventoDTO editarEventoDTO) throws EventoException;

    void eliminarEvento(String id) throws EventoException;

    Evento obtenerInformacionEvento(String id) throws EventoException;

    List<ItemEventoDTO> listarEventos();

    List<EventoFiltradoDTO> filtrarEventos(EventoFiltradoDTO filtro);

}
