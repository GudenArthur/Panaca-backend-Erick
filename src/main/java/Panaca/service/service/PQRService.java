package Panaca.service.service;

import Panaca.dto.PQR.CrearPQRDTO;
import Panaca.dto.PQR.InformacionPQRDTO;
import Panaca.dto.PQR.ItemPQRDTO;
import Panaca.dto.PQR.ResponderPQRDTO;
import Panaca.model.documents.PQR;
import Panaca.exceptions.PQRException;

import java.util.List;

public interface PQRService {

    void crearPQR(CrearPQRDTO pqrDTO) throws PQRException;

    void eliminarPQR(String id) throws PQRException;

    InformacionPQRDTO obtenerInformacionPQR(String id) throws PQRException;

    List<PQR> obtenerPQRsPorUsuario(String idUsuario) throws PQRException;

    void responderPQR(ResponderPQRDTO responderPqrDTO) throws PQRException;

    List<ItemPQRDTO> listarPQRs() throws PQRException;

}
