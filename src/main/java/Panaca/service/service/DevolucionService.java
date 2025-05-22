package Panaca.service.service;

import Panaca.dto.devolucion.DevolucionRequestDTO;
import Panaca.dto.devolucion.DevolucionResponseDTO;

import java.util.List;

public interface DevolucionService {

    DevolucionResponseDTO solicitar(DevolucionRequestDTO dto);

    List<DevolucionResponseDTO> listarPorUsuario(String cuentaId);

    List<DevolucionResponseDTO> listarTodas();

    DevolucionResponseDTO aprobar(String id);

    DevolucionResponseDTO rechazar(String id);
}