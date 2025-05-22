package Panaca.test;

import Panaca.dto.autenticacion.TokenDTO;
import Panaca.dto.cuenta.*;
import Panaca.model.documents.Carrito;
import Panaca.model.documents.Cuenta;
import Panaca.model.enums.EstadoCuenta;
import Panaca.model.enums.Rol;
import Panaca.repository.CarritoRepository;
import Panaca.repository.CuentaRepository;
import Panaca.repository.CuponRepository;
import Panaca.service.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CuentaServiceTest {

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CuponRepository cuponRepository;

    @Test
    public void crearCuentaTest() {
        // Datos únicos para evitar conflictos por claves duplicadas
        String email = UUID.randomUUID() + "@test.com";
        String cedula = String.valueOf((int)(Math.random() * 1_000_000_000)); // 9 dígitos aleatorios

        CrearCuentaDTO cuentaDTO = new CrearCuentaDTO(
                cedula,
                "Juan Test",
                "3123456789",
                email,
                "password123"
        );

        assertDoesNotThrow(() -> {
            cuentaService.crearCuenta(cuentaDTO);
        });

        // Verificar que la cuenta fue guardada
        Optional<Cuenta> cuentaOpt = cuentaRepository.findByEmail(email);
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuenta = cuentaOpt.get();
        assertEquals(email, cuenta.getEmail());
        assertEquals(EstadoCuenta.INACTIVO, cuenta.getEstado());
        assertEquals(Rol.CLIENTE, cuenta.getRol());

        // Verificar que el carrito fue creado correctamente
        Optional<Carrito> carritoOpt = carritoRepository.findByIdUsuario(cuenta.getId());
        assertTrue(carritoOpt.isPresent());

        Carrito carrito = carritoOpt.get();
        assertEquals(cuenta.getId(), carrito.getIdUsuario());
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    public void crearCuenta_conEmailExistente_debeLanzarExcepcion() {
        // Crear primero una cuenta válida
        String email = UUID.randomUUID() + "@test.com";
        String cedula = String.valueOf((int)(Math.random() * 1_000_000_000));

        CrearCuentaDTO cuentaDTO = new CrearCuentaDTO(
                cedula,
                "Usuario Original",
                "3000000000",
                email,
                "password123"
        );

        assertDoesNotThrow(() -> cuentaService.crearCuenta(cuentaDTO));

        // Intentar crear otra cuenta con mismo email pero distinta cédula
        CrearCuentaDTO cuentaDTOEmailDuplicado = new CrearCuentaDTO(
                String.valueOf((int)(Math.random() * 1_000_000_000)), // cédula distinta
                "Usuario Duplicado",
                "3111111111",
                email, // mismo email
                "password123"
        );

        Exception ex = assertThrows(Exception.class, () -> cuentaService.crearCuenta(cuentaDTOEmailDuplicado));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    public void crearCuenta_conCedulaExistente_debeLanzarExcepcion() {
        // Crear primero una cuenta válida
        String email = UUID.randomUUID() + "@test.com";
        String cedula = String.valueOf((int)(Math.random() * 1_000_000_000));

        CrearCuentaDTO cuentaDTO = new CrearCuentaDTO(
                cedula,
                "Usuario Original",
                "3000000000",
                email,
                "password123"
        );

        assertDoesNotThrow(() -> cuentaService.crearCuenta(cuentaDTO));

        // Intentar crear otra cuenta con misma cédula pero distinto email
        CrearCuentaDTO cuentaDTOCedulaDuplicada = new CrearCuentaDTO(
                cedula, // misma cédula
                "Usuario Duplicado",
                "3222222222",
                UUID.randomUUID() + "@test.com", // email distinto
                "password123"
        );

        Exception ex = assertThrows(Exception.class, () -> cuentaService.crearCuenta(cuentaDTOCedulaDuplicada));
        assertTrue(ex.getMessage().contains("cedula"));
    }

    @Test
    public void editarCuentaTest() {
        // Crear cuenta de prueba
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Pepito Original");
        cuenta.setCedula("987654321");
        cuenta.setTelefono("3000000000");
        cuenta.setEmail(UUID.randomUUID().toString() + "@test.com");
        cuenta.setPassword(new BCryptPasswordEncoder().encode("originalpass"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta = cuentaRepository.save(cuenta); // guardamos y capturamos el ID

        EditarCuentaDTO editarDTO = new EditarCuentaDTO(
                cuenta.getId(),
                "Pepito Perez Actualizado",
                "3012345678",
                "nuevapassword123"
        );

        assertDoesNotThrow(() -> cuentaService.editarCuenta(editarDTO));

        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(cuenta.getId());
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuentaActualizada = cuentaOpt.get();
        assertEquals("Pepito Perez Actualizado", cuentaActualizada.getNombre());
        assertEquals("3012345678", cuentaActualizada.getTelefono());
        assertNotEquals("nuevapassword123", cuentaActualizada.getPassword()); // debe estar encriptada
    }


    @Test
    public void editarCuenta_conIdInexistente_debeLanzarExcepcion() {
        EditarCuentaDTO editarDTO = new EditarCuentaDTO(
                "000000000000000000000000", // ID inexistente
                "Nombre Invalido",
                "3000000000",
                "otraPassword123"
        );

        Exception ex = assertThrows(Exception.class, () -> cuentaService.editarCuenta(editarDTO));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    public void eliminarCuentaTest() {
        // Crear cuenta activa
        String email = UUID.randomUUID().toString() + "@test.com";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Eliminar Prueba");
        cuenta.setCedula("987123456");
        cuenta.setTelefono("3009999999");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("eliminarpass"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta = cuentaRepository.save(cuenta);

        final String idCuenta = cuenta.getId(); // declarar final para usar en lambda

        assertDoesNotThrow(() -> cuentaService.eliminarCuenta(idCuenta));

        Optional<Cuenta> cuentaOpt = cuentaRepository.findById(idCuenta);
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuentaEliminada = cuentaOpt.get();
        assertEquals(EstadoCuenta.INACTIVO, cuentaEliminada.getEstado());
    }


    @Test
    public void eliminarCuenta_conIdInexistente_debeLanzarExcepcion() {
        String idFalso = "000000000000000000000000";

        Exception ex = assertThrows(Exception.class, () -> cuentaService.eliminarCuenta(idFalso));
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    public void obtenerInformacionCuentaTest() {
        // Crear cuenta de prueba
        String email = UUID.randomUUID().toString() + "@test.com";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Pepito Perez Actualizado");
        cuenta.setCedula("531489543");
        cuenta.setTelefono("3012345678");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("password123"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta = cuentaRepository.save(cuenta); // importante reasignar

        final String idCuenta = cuenta.getId(); // necesario para usarlo en lambda

        InformacionCuentaDTO info = assertDoesNotThrow(() -> cuentaService.obtenerInformacionCuenta(idCuenta));

        assertEquals(idCuenta, info.id());
        assertEquals("531489543", info.cedula());
        assertEquals("Pepito Perez Actualizado", info.nombre());
        assertEquals("3012345678", info.telefono());
        assertEquals(email, info.email());
    }

    @Test
    public void obtenerInformacionCuenta_conIdInexistente_debeLanzarExcepcion() {
        String idFalso = "000000000000000000000000";

        Exception ex = assertThrows(Exception.class, () -> cuentaService.obtenerInformacionCuenta(idFalso));
        assertTrue(ex.getMessage().contains("id"));
    }

    @Test
    public void enviarCodigoRecuperacionPasswordTest() {
        // Crear cuenta de prueba
        final String email = UUID.randomUUID().toString() + "@test.com";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Usuario Recuperacion");
        cuenta.setCedula("741852963");
        cuenta.setTelefono("3011112233");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("passRecuperar123"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuentaRepository.save(cuenta);

        CodigoContraseniaDTO dto = new CodigoContraseniaDTO(email);

        assertDoesNotThrow(() -> cuentaService.enviarCodigoRecuperacionPassword(dto));

        Optional<Cuenta> cuentaOpt = cuentaRepository.findByEmail(email);
        assertTrue(cuentaOpt.isPresent());

        Cuenta cuentaActualizada = cuentaOpt.get();
        assertNotNull(cuentaActualizada.getCodigoVerificacionContrasenia());
        assertNotNull(cuentaActualizada.getFechaExpiracionCodigoContrasenia());
    }

    @Test
    public void enviarCodigoRecuperacionPassword_emailInvalido_debeLanzarExcepcion() {
        String email = "noexiste@test.com";

        CodigoContraseniaDTO dto = new CodigoContraseniaDTO(email);

        Exception ex = assertThrows(Exception.class, () -> cuentaService.enviarCodigoRecuperacionPassword(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("cuenta"));
    }

    @Test
    public void cambiarPassword_codigoValido_actualizaContrasenia() {
        final String email = UUID.randomUUID().toString() + "@test.com";

        // Crear cuenta con código válido
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Cambio Password Valido");
        cuenta.setCedula("123123123");
        cuenta.setTelefono("3111111111");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("viejaPass123"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta.setCodigoVerificacionContrasenia("ABCDE");
        cuenta.setFechaExpiracionCodigoContrasenia(LocalDateTime.now().plusMinutes(10)); // válido
        cuentaRepository.save(cuenta);

        CambiarPasswordDTO dto = new CambiarPasswordDTO("ABCDE", "nuevaPass123");

        assertDoesNotThrow(() -> cuentaService.cambiarPassword(dto));

        Cuenta cuentaActualizada = cuentaRepository.findByEmail(email).get();
        assertNull(cuentaActualizada.getCodigoVerificacionContrasenia());
        assertNull(cuentaActualizada.getFechaExpiracionCodigoContrasenia());
    }

    @Test
    public void cambiarPassword_codigoInexistente_lanzaExcepcion() {
        CambiarPasswordDTO dto = new CambiarPasswordDTO("XXXX", "otraClave123");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.cambiarPassword(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("inválido"));
    }

    @Test
    public void cambiarPassword_codigoExpirado_lanzaExcepcionYReenvia() {
        final String email = UUID.randomUUID().toString() + "@test.com";

        // Crear cuenta con código expirado
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Cambio Password Expirado");
        cuenta.setCedula("321321321");
        cuenta.setTelefono("3222222222");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("viejaPass123"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta.setCodigoVerificacionContrasenia("ZZZZZ");
        cuenta.setFechaExpiracionCodigoContrasenia(LocalDateTime.now().minusMinutes(1)); // ya expiró
        cuentaRepository.save(cuenta);

        CambiarPasswordDTO dto = new CambiarPasswordDTO("ZZZZZ", "claveNueva2024");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.cambiarPassword(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("expirado"));
    }

    @Test
    public void validarCodigo_codigoCorrecto_activaCuentaYGeneraCupon() {
        final String email = UUID.randomUUID().toString() + "@test.com";

        // Crear cuenta con código válido
        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Usuario Validacion Correcta");
        cuenta.setCedula("123456789");
        cuenta.setTelefono("3001112233");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("password123"));
        cuenta.setEstado(EstadoCuenta.INACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta.setCodigoVerificacionRegistro("ABCDE");
        cuenta.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(10));
        cuentaRepository.save(cuenta);

        ValidarCodigoDTO dto = new ValidarCodigoDTO(email, "ABCDE");

        assertDoesNotThrow(() -> cuentaService.validarCodigo(dto));

        Cuenta actualizada = cuentaRepository.findByEmail(email).get();
        assertEquals(EstadoCuenta.ACTIVO, actualizada.getEstado());
        assertNull(actualizada.getCodigoVerificacionRegistro());
        assertNull(actualizada.getFechaExpiracionCodigo());

        // Verificamos que se haya creado al menos un cupón
        assertFalse(cuponRepository.findAll().isEmpty());
    }

    @Test
    public void validarCodigo_codigoIncorrecto_lanzaExcepcion() {
        final String email = UUID.randomUUID().toString() + "@test.com";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Usuario Codigo Incorrecto");
        cuenta.setCedula("987654321");
        cuenta.setTelefono("3002223344");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("password123"));
        cuenta.setEstado(EstadoCuenta.INACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta.setCodigoVerificacionRegistro("ABCDE");
        cuenta.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(10));
        cuentaRepository.save(cuenta);

        ValidarCodigoDTO dto = new ValidarCodigoDTO(email, "ZZZZZ");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.validarCodigo(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("incorrecto"));
    }

    @Test
    public void validarCodigo_codigoExpirado_lanzaExcepcionYReenvia() {
        final String email = UUID.randomUUID().toString() + "@test.com";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Usuario Codigo Expirado");
        cuenta.setCedula("456123789");
        cuenta.setTelefono("3005556677");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("password123"));
        cuenta.setEstado(EstadoCuenta.INACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuenta.setCodigoVerificacionRegistro("ABCDE");
        cuenta.setFechaExpiracionCodigo(LocalDateTime.now().minusMinutes(1)); // ya expirado
        cuentaRepository.save(cuenta);

        ValidarCodigoDTO dto = new ValidarCodigoDTO(email, "ABCDE");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.validarCodigo(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("expirado"));
    }

    @Test
    public void iniciarSesion_loginValido_retornaToken() {
        final String email = UUID.randomUUID().toString() + "@test.com";
        final String passwordPlano = "password123";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Usuario Login OK");
        cuenta.setCedula("112233445");
        cuenta.setTelefono("3006667788");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode(passwordPlano));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuentaRepository.save(cuenta);

        LoginDTO dto = new LoginDTO(email, passwordPlano);

        TokenDTO tokenDTO = assertDoesNotThrow(() -> cuentaService.iniciarSesion(dto));
        assertNotNull(tokenDTO.token());
    }

    @Test
    public void iniciarSesion_emailInexistente_lanzaExcepcion() {
        LoginDTO dto = new LoginDTO("noexiste@test.com", "cualquierpass");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.iniciarSesion(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("correo"));
    }

    @Test
    public void iniciarSesion_passwordIncorrecta_lanzaExcepcion() {
        final String email = UUID.randomUUID().toString() + "@test.com";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Usuario Password Incorrecta");
        cuenta.setCedula("998877665");
        cuenta.setTelefono("3008889999");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode("passwordCorrecta"));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setRol(Rol.CLIENTE);
        cuentaRepository.save(cuenta);

        LoginDTO dto = new LoginDTO(email, "passwordIncorrecta");

        Exception ex = assertThrows(Exception.class, () -> cuentaService.iniciarSesion(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("incorrecta"));
    }

    @Test
    public void iniciarSesion_cuentaInactiva_lanzaExcepcion() {
        final String email = UUID.randomUUID().toString() + "@test.com";
        final String passwordPlano = "password123";

        Cuenta cuenta = new Cuenta();
        cuenta.setNombre("Usuario Inactivo");
        cuenta.setCedula("554433221");
        cuenta.setTelefono("3007778899");
        cuenta.setEmail(email);
        cuenta.setPassword(new BCryptPasswordEncoder().encode(passwordPlano));
        cuenta.setEstado(EstadoCuenta.INACTIVO); // importante
        cuenta.setRol(Rol.CLIENTE);
        cuentaRepository.save(cuenta);

        LoginDTO dto = new LoginDTO(email, passwordPlano);

        Exception ex = assertThrows(Exception.class, () -> cuentaService.iniciarSesion(dto));
        assertTrue(ex.getMessage().toLowerCase().contains("no está activa"));
    }

}