package Panaca.repository;

import Panaca.model.documents.Cupon;
import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.TipoCupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuponRepository extends MongoRepository<Cupon, String> {

    boolean existsByCodigo(String codigo);

    List<Cupon> findByNombreContainingIgnoreCase(String nombre);
    List<Cupon> findByFechaVencimientoAfter(LocalDate fechaVencimiento);
    List<Cupon> findByFechaAperturaAfter(LocalDate fechaApertura);
    List<Cupon> findByDescuento(Float descuento);
    List<Cupon> findByTipo(TipoCupon tipo);
    List<Cupon> findByEstado(EstadoCupon estado);
    Optional<Cupon> findByCodigo(String codigo);
    Page<Cupon> findByEstado(EstadoCupon estado, Pageable pageable);
}
