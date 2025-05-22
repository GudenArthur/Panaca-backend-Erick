package Panaca.test;

import Panaca.dto.donation.DonationDTO;
import Panaca.dto.donation.DonationItemDTO;
import Panaca.dto.donation.MostrarDonacionDTO;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.Donation;
import Panaca.model.enums.AnimalType;
import Panaca.model.enums.EstadoCuenta;
import Panaca.model.enums.Rol;
import Panaca.repository.CuentaRepository;
import Panaca.repository.DonationRepository;
import Panaca.service.service.DonationService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class DonationServiceTest {

    private Cuenta cuenta;

    @Autowired
    DonationService donationService;

    @Autowired
    DonationRepository donationRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setNombre("Test Donador");
        cuenta.setEmail("donador@test.com");
        cuenta.setTelefono("3000000000");
        cuenta.setPassword("segura123");
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta = cuentaRepository.save(cuenta);
    }

    @Test
    void crearDonacion_datosValidos_guardaCorrectamente() {
        DonationItemDTO item = new DonationItemDTO(AnimalType.GATO, 2);
        DonationDTO dto = new DonationDTO(cuenta.getId(), List.of(item));

        assertDoesNotThrow(() -> donationService.crearDonacion(dto));

        List<Donation> donaciones = donationRepository.findByIdDonanteOrderByFechaDesc(cuenta.getId());
        assertFalse(donaciones.isEmpty());
        assertEquals(1, donaciones.get(0).getItems().size());
        assertEquals(AnimalType.GATO, donaciones.get(0).getItems().get(0).getTipoAnimal());
    }

    @Test
    void crearDonacion_cuentaInvalida_lanzaExcepcion() {
        DonationItemDTO item = new DonationItemDTO(AnimalType.PERRO, 1);
        DonationDTO dto = new DonationDTO("id-falso", List.of(item));

        Exception ex = assertThrows(RuntimeException.class, () ->
                donationService.crearDonacion(dto));

        assertEquals("La cuenta del donante no existe", ex.getMessage());
    }

    @Test
    void realizarPagoDonacion_donacionInexistente_lanzaExcepcion() {
        String idFalso = new ObjectId().toString();

        Exception ex = assertThrows(RuntimeException.class, () ->
                donationService.realizarPagoDonacion(idFalso));

        assertEquals("No se encontró la donación con el ID: " + idFalso, ex.getMessage());
    }

    @Test
    void obtenerHistorialDonaciones_idValido_retornaListaCorrecta() {
        DonationItemDTO item = new DonationItemDTO(AnimalType.CONEJO, 3);
        donationService.crearDonacion(new DonationDTO(cuenta.getId(), List.of(item)));

        List<MostrarDonacionDTO> historial = donationService.obtenerHistorialDonaciones(cuenta.getId());

        assertFalse(historial.isEmpty());
        MostrarDonacionDTO donacion = historial.get(0);
        assertEquals(1, donacion.items().size());
        assertEquals("Conejo", donacion.items().get(0).animal());
    }

    @Test
    void obtenerHistorialDonaciones_idInvalido_lanzaExcepcion() {
        String idFalso = new ObjectId().toString();

        Exception ex = assertThrows(RuntimeException.class, () ->
                donationService.obtenerHistorialDonaciones(idFalso));

        assertEquals("No se encontró la cuenta con ID: " + idFalso, ex.getMessage());
    }
}