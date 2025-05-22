package Panaca.repository;

import Panaca.model.documents.Evento;
import Panaca.model.enums.EstadoEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends MongoRepository<Evento, String> {

    //Metodo para encontrar un evento por nombre
    List<Evento> findByNombre(String nombre);

    Page<Evento> getEventosByEstado(EstadoEvento estadoEvento, Pageable pageable);

    @Query("{"
            + "'$or': ["
            + "   { 'nombre': { $regex: ?0, $options: 'i' } },"
            + "   { 'tipo': { $eq: ?1 } },"
            + "]"
            + "}")

    List<Evento> filtrarEventos(String nombre, String tipoEvento);
}