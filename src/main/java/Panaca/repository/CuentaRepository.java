package Panaca.repository;

import Panaca.model.documents.Cuenta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface CuentaRepository extends MongoRepository<Cuenta, String>{

    long countByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);

    boolean existsByEmail(String email);

    boolean existsByCedula(String cedula);


    Optional<Cuenta> findByEmail(String correo);

    @Query("{'cedula': ?0}")
    Optional<Cuenta> findByCedula(String cedula);

    @Query("{'codigoVerificacionContrasenia': ?0}")
    Optional<Cuenta> existBycodigoVerificacionContrasenia(String codigoVerificacionContrasenia);

    Optional<Cuenta> findByCodigoVerificacionContrasenia(String codigoVerificacionContrasenia);

}
