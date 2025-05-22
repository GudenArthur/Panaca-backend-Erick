package Panaca.service.implement;

import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.model.documents.*;
import Panaca.model.enums.EstadoDevolucion;
import Panaca.repository.*;
import Panaca.service.service.DevolucionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Validated

public class DevolucionServiceImp implements DevolucionService {

    private final DevolucionRequestRepository devolucionRepo;
    private final CuentaRepository cuentaRepo;
    private final OrdenRepository ordenRepo;
    private final DonationRepository donationRepo;

    @Override
    public DevolucionResponseDTO solicitar(DevolucionRequestDTO dto) {
        // Validación de existencia de cuenta
        Cuenta cuenta = cuentaRepo.findById(dto.cuentaId())
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Validar si ya existe una solicitud pendiente para esa referencia
        if (devolucionRepo.existsByReferenciaIdAndEstado(dto.referenciaId(), EstadoDevolucion.PENDIENTE)) {
            throw new RuntimeException("Ya existe una solicitud pendiente para esta referencia");
        }

        // Validaciones específicas según el tipo
        switch (dto.tipo()) {
            case TICKET -> validarTicketParaDevolucion(dto.referenciaId());
            case DONACION -> validarDonacionParaDevolucion(dto.referenciaId());
            default -> throw new RuntimeException("Tipo de devolución no reconocido");
        }

        // Crear solicitud
        DevolucionRequest solicitud = new DevolucionRequest();
        solicitud.setIdCuenta(cuenta.getId());
        solicitud.setTipo(dto.tipo());
        solicitud.setReferenciaId(dto.referenciaId());
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado(EstadoDevolucion.PENDIENTE);

        DevolucionRequest saved = devolucionRepo.save(solicitud);
        return toResponse(saved);
    }

    private void validarTicketParaDevolucion(String idOrden) {
        Orden orden = ordenRepo.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        LocalDate fechaUso = orden.getDetalle().get(0).getFechaUso();

        if (LocalDate.now().isAfter(fechaUso.minusWeeks(1))) {
            throw new RuntimeException("Ya no es posible solicitar devolución de ticket");
        }
    }

    private void validarDonacionParaDevolucion(String idDonacion) {
        Donation donation = donationRepo.findById(idDonacion)
                .orElseThrow(() -> new RuntimeException("Donación no encontrada"));

        if (LocalDateTime.now().isAfter(donation.getFecha().plusHours(24))) {
            throw new RuntimeException("Pasaron más de 24h para solicitar devolución de donación");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionResponseDTO> listarPorUsuario(String cuentaId) {
        return devolucionRepo.findByIdCuenta(cuentaId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionResponseDTO> listarTodas() {
        return devolucionRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public DevolucionResponseDTO aprobar(String id) {
        DevolucionRequest request = devolucionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (request.getEstado() != EstadoDevolucion.PENDIENTE) {
            throw new RuntimeException("La solicitud ya fue procesada");
        }

        request.setEstado(EstadoDevolucion.APROBADA);
        return toResponse(devolucionRepo.save(request));
    }

    @Override
    public DevolucionResponseDTO rechazar(String id) {
        DevolucionRequest request = devolucionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (request.getEstado() != EstadoDevolucion.PENDIENTE) {
            throw new RuntimeException("La solicitud ya fue procesada");
        }

        request.setEstado(EstadoDevolucion.RECHAZADA);
        return toResponse(devolucionRepo.save(request));
    }

    private DevolucionResponseDTO toResponse(DevolucionRequest r) {
        return new DevolucionResponseDTO(
                r.getId(),
                r.getTipo(),
                r.getReferenciaId(),
                r.getFechaSolicitud(),
                r.getEstado()
        );
    }
}
