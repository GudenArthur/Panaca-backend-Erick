package Panaca.repository;

import Panaca.model.documents.DevolucionRequest;
import Panaca.model.enums.EstadoDevolucion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DevolucionRequestRepository extends MongoRepository<DevolucionRequest, String> {
    List<DevolucionRequest> findByIdCuenta(String cuentaId);

    boolean existsByReferenciaIdAndEstado(String referenciaId, EstadoDevolucion estado);

}
