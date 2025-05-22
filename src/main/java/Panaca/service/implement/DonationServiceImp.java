package Panaca.service.implement;

import Panaca.configs.MercadoPagoProperties;
import Panaca.dto.donation.DonationDTO;
import Panaca.dto.donation.ItemDonacionDTO;
import Panaca.dto.donation.MostrarDonacionDTO;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.Donation;
import Panaca.model.documents.DonationItem;
import Panaca.repository.CuentaRepository;
import Panaca.repository.DonationRepository;
import Panaca.service.service.DonationService;
import Panaca.service.service.EmailService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Validated

public class DonationServiceImp implements DonationService {

    private final DonationRepository donationRepo;
    private final CuentaRepository cuentaRepo;
    private final MercadoPagoProperties mercadoPagoProperties;

    public void crearDonacion(DonationDTO donationDTO) {
        Cuenta donante = cuentaRepo.findById(donationDTO.donanteId())
                .orElseThrow(() -> new RuntimeException("La cuenta del donante no existe"));

        Donation donation = new Donation();
        donation.setIdDonante(donante.getId());
        donation.setFecha(LocalDateTime.now());

        List<DonationItem> items = donationDTO.items().stream().map(itemDTO -> {
            int unitPrice = itemDTO.tipoAnimal().getPricePerBulto();
            int subtotal = unitPrice * itemDTO.cantidadBultos();
            return new DonationItem(
                    null,
                    null, // se setea después
                    itemDTO.tipoAnimal(),
                    itemDTO.cantidadBultos(),
                    unitPrice,
                    subtotal
            );
        }).toList();

        // asignamos relación hacia atrás
        items.forEach(item -> item.setDonationId(donation.getId()));

        donation.setItems(items);
        donation.setTotal(items.stream().mapToInt(DonationItem::getSubtotal).sum());

        donationRepo.save(donation);
    }

    @Override
    public Preference realizarPagoDonacion(String idDonacion) throws Exception {
        Donation donacion = donationRepo.findById(idDonacion)
                .orElseThrow(() -> new RuntimeException("No se encontró la donación con el ID: " + idDonacion));

        Cuenta cuenta = cuentaRepo.findById(donacion.getIdDonante())
                .orElseThrow(() -> new RuntimeException("No se encontró la cuenta del donante"));

        List<PreferenceItemRequest> itemsPasarela = donacion.getItems().stream().map(item ->
                PreferenceItemRequest.builder()
                        .title("Donación para " + item.getTipoAnimal().getDisplayName())
                        .quantity(item.getCantidadBultos())
                        .currencyId("COP")
                        .unitPrice(BigDecimal.valueOf(item.getPrecioUnitario()))
                        .build()
        ).toList();

        MercadoPagoConfig.setAccessToken(mercadoPagoProperties.getToken());

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://tusitio.com/pago/exito")
                .failure("https://tusitio.com/pago/fallo")
                .pending("https://tusitio.com/pago/pendiente")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .backUrls(backUrls)
                .items(itemsPasarela)
                .metadata(Map.of("id_donacion", donacion.getId()))
                .notificationUrl(mercadoPagoProperties.getNotificationUrl())
                .build();

        PreferenceClient client = new PreferenceClient();
        return client.create(preferenceRequest);
    }

    @Override
    public List<MostrarDonacionDTO> obtenerHistorialDonaciones(String idCuenta) {
        if (!cuentaRepo.existsById(idCuenta)) {
            throw new RuntimeException("No se encontró la cuenta con ID: " + idCuenta);
        }

        return donationRepo.findByIdDonanteOrderByFechaDesc(idCuenta).stream()
                .map(donacion -> new MostrarDonacionDTO(
                        donacion.getId(),
                        donacion.getFecha(),
                        donacion.getTotal(),
                        donacion.getItems().stream()
                                .map(item -> new ItemDonacionDTO(
                                        item.getTipoAnimal().getDisplayName(),
                                        item.getCantidadBultos(),
                                        item.getSubtotal()
                                ))
                                .toList()
                )).toList();
    }
}