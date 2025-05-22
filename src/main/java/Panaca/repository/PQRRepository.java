package Panaca.repository;

import Panaca.model.documents.PQR;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PQRRepository extends MongoRepository<PQR, String> {

    @Query("{'idUsuario': ?0}")
    List<PQR> findByIdUsuario(String idUsuario);

    @Query("{'id': ?0}")
    Optional<PQR> findById(String id);
}