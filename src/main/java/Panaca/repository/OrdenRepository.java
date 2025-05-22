package Panaca.repository;

import Panaca.model.documents.Orden;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends MongoRepository<Orden, String> {

    List<Orden> findByIdCliente(String idCliente);

    @Query("{ 'idOrden': ?0 }")
    List<Orden> findByIdOrden(String idOrden);

}
