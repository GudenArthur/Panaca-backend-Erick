package Panaca.service.service;

import Panaca.dto.donation.DonationDTO;
import Panaca.dto.donation.MostrarDonacionDTO;
import com.mercadopago.resources.preference.Preference;

import java.util.List;

public interface DonationService {

    void crearDonacion(DonationDTO donationDTO);

    Preference realizarPagoDonacion(String idDonacion) throws Exception;

    List<MostrarDonacionDTO> obtenerHistorialDonaciones(String idCuenta);

}
