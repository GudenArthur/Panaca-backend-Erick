package Panaca.service.implement;

import Panaca.dto.PQR.CrearPQRDTO;
import Panaca.dto.PQR.InformacionPQRDTO;
import Panaca.dto.PQR.ItemPQRDTO;
import Panaca.dto.PQR.ResponderPQRDTO;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.PQR;
import Panaca.model.enums.EstadoPQR;
import Panaca.service.service.PQRService;
import Panaca.exceptions.PQRException;
import Panaca.repository.CuentaRepository;
import Panaca.repository.PQRRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated

public class PQRServiceImp implements PQRService {

    private final PQRRepository pqrRepository;
    private final CuentaRepository cuentaRepository;

    @Override
    public void crearPQR(CrearPQRDTO pqrDTO) throws PQRException {
        try {
            PQR pqr = new PQR();
            pqr.setIdUsuario(pqrDTO.idUsuario());
            pqr.setFechaCreacion(LocalDateTime.now());
            pqr.setEstadoPQR(EstadoPQR.PENDIENTE);
            pqr.setCategoriaPQR(pqrDTO.categoriaPQR());
            pqr.setDescripcion(pqrDTO.descripcion());
            pqr.setRespuesta(null);
            pqr.setFechaRespuesta(null);

            pqrRepository.save(pqr);
        } catch (Exception e) {
            throw new PQRException("Error creando la PQR: " + e);
        }
    }

    @Override
    public void eliminarPQR(String id) throws PQRException {
        PQR pqr = pqrRepository.findById(id)
                .orElseThrow(() -> new PQRException("PQR no encontrada"));

        pqr.setEstadoPQR(EstadoPQR.ELIMINADO);
        pqrRepository.save(pqr);
    }

    @Override
    public InformacionPQRDTO obtenerInformacionPQR(String id) throws PQRException {
        PQR pqr = pqrRepository.findById(id)
                .orElseThrow(() -> new PQRException("PQR no encontrada"));

        Cuenta cuenta = cuentaRepository.findById(pqr.getIdUsuario())
                .orElseThrow(() -> new PQRException("Usuario no encontrado para la PQR"));

        return new InformacionPQRDTO(
                pqr.getId(),
                cuenta.getNombre(),
                cuenta.getTelefono(),
                cuenta.getEmail(),
                pqr.getFechaCreacion(),
                pqr.getEstadoPQR(),
                pqr.getCategoriaPQR(),
                pqr.getDescripcion(),
                pqr.getRespuesta(),
                pqr.getFechaRespuesta()
        );
    }

    @Override
    public List<PQR> obtenerPQRsPorUsuario(String idUsuario) throws PQRException {
        List<PQR> pqrs = pqrRepository.findByIdUsuario(idUsuario);
        if (pqrs.isEmpty()) {
            throw new PQRException("No se encontraron PQRs para el usuario con ID: " + idUsuario);
        }
        return pqrs;
    }

    @Override
    public void responderPQR(ResponderPQRDTO responderPqrDTO) throws PQRException {
        PQR pqr = pqrRepository.findById(responderPqrDTO.id())
                .orElseThrow(() -> new PQRException("PQR no encontrada"));

        if (pqr.getEstadoPQR() == EstadoPQR.RESUELTO) {
            throw new PQRException("La PQR ya ha sido resuelta");
        }

        pqr.setRespuesta(responderPqrDTO.respuesta());
        pqr.setEstadoPQR(EstadoPQR.RESUELTO);
        pqr.setFechaRespuesta(LocalDateTime.now());

        pqrRepository.save(pqr);
    }

    @Override
    public List<ItemPQRDTO> listarPQRs() throws PQRException {
        List<PQR> pqrs = pqrRepository.findAll();

        if (pqrs.isEmpty()) {
            throw new PQRException("No hay PQRs registradas en el sistema.");
        }

        return pqrs.stream()
                .map(pqr -> new ItemPQRDTO(
                        pqr.getId(),
                        pqr.getIdUsuario(),
                        pqr.getEstadoPQR(),
                        pqr.getFechaCreacion()
                ))
                .collect(Collectors.toList());
    }
}