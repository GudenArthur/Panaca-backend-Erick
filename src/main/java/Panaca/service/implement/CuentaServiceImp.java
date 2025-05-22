package Panaca.service.implement;

import Panaca.configs.JWTUtils;
import Panaca.dto.autenticacion.TokenDTO;
import Panaca.dto.carrito.CrearCarritoDTO;
import Panaca.dto.cuenta.*;
import Panaca.dto.email.EmailDTO;
import Panaca.model.documents.Cuenta;
import Panaca.model.documents.Cupon;
import Panaca.model.enums.EstadoCuenta;
import Panaca.model.enums.EstadoCupon;
import Panaca.model.enums.Rol;
import Panaca.model.enums.TipoCupon;
import Panaca.service.service.CarritoService;
import Panaca.service.service.CuentaService;
import Panaca.service.service.EmailService;
import Panaca.exceptions.CarritoException;
import Panaca.exceptions.CuentaException;
import Panaca.repository.CuentaRepository;
import Panaca.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Validated

public class CuentaServiceImp implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final CarritoService carritoService;
    private final JWTUtils jwtUtils;
    private final EmailService emailService;
    private final CuponRepository cuponRepository;

    // Metodo de apoyo para buscar una cuenta por ID y devolver un Optional<Cuenta>
    private Optional<Cuenta> obtenerCuentaPorId(String idCuenta) {
        return cuentaRepository.findById(idCuenta);
    }

    /**
     * Metodo para crear una cuenta
     * param cuentaDTO
     * throws CuentaException si la cuenta validada por el email ya existe
     */
    @Override
    public void crearCuenta(CrearCuentaDTO cuentaDTO) throws CuentaException, CarritoException {
        // Verifica si ya existe una cuenta con el mismo email
        if (cuentaRepository.existsByEmail(cuentaDTO.email())) {
            throw new CuentaException("Ya existe una cuenta registrada con este email electrónico.");
        }
        if(cuentaRepository.existsByCedula(cuentaDTO.cedula())){
            throw new CuentaException("Ya existe una cuenta registrada con esta cedula.");
        }

        // Crear instancia de Cuenta con datos del DTO
        Cuenta nuevaCuenta = new Cuenta();
        nuevaCuenta.setCedula(cuentaDTO.cedula());
        nuevaCuenta.setNombre(cuentaDTO.nombre());
        nuevaCuenta.setTelefono(cuentaDTO.telefono());
        nuevaCuenta.setEmail(cuentaDTO.email());

        String encrptar = encriptarPassword(cuentaDTO.password());
        nuevaCuenta.setPassword(encrptar);

        nuevaCuenta.setFechaRegistro(LocalDateTime.now());
        nuevaCuenta.setRol(Rol.CLIENTE);
        nuevaCuenta.setEstado(EstadoCuenta.INACTIVO);

        // Generar y asignar el código de validación
        String codigoValidacion = generarCodigoValidacion();
        nuevaCuenta.setCodigoVerificacionRegistro(codigoValidacion);
        nuevaCuenta.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(10));

        // Guardar la cuenta en la base de datos
        Cuenta cuentaCreada = cuentaRepository.save(nuevaCuenta);

        // Lógica para crear el carrito automáticamente
        CrearCarritoDTO carritoDTO = new CrearCarritoDTO(
                cuentaCreada.getId(),
                new ArrayList<>()
        );
        carritoService.crearCarrito(carritoDTO);

        // Enviar el código de validación al correo
        String asunto = "Código de Validación";
        String cuerpo = "Tu código de validación es: " + codigoValidacion;
        EmailDTO emailDTO = new EmailDTO(asunto, cuerpo, cuentaDTO.email());
        emailService.enviarCorreo(emailDTO);

    }

    /**
     * Metodo para validar que el codigo de el usuario sea el enviado a el correo
     * param email
     * param codigo
     * return
     * throws CuentaException
     */
    @Override
    public ValidarCodigoDTO validarCodigo(ValidarCodigoDTO validarCodigoDTO) throws CuentaException {
        // Buscar la cuenta por email
        Cuenta cuenta = cuentaRepository.findByEmail(validarCodigoDTO.email())
                .orElseThrow(() -> new CuentaException("No se encontró la cuenta."));

        // Verificar si el código ha expirado
        if (cuenta.getFechaExpiracionCodigo() != null && LocalDateTime.now().isAfter(cuenta.getFechaExpiracionCodigo())) {
            // El código ha expirado, genera uno nuevo
            String nuevoCodigo = generarCodigoValidacion();
            cuenta.setCodigoVerificacionRegistro(nuevoCodigo);
            cuenta.setFechaExpiracionCodigo(LocalDateTime.now().plusMinutes(10)); // Expira en 10 minutos
            cuentaRepository.save(cuenta);

            // Enviar el nuevo código al correo
            String asunto = "Nuevo Código de Validación";
            String cuerpo = "Tu nuevo código de validación es: " + nuevoCodigo;
            EmailDTO emailDTO = new EmailDTO(asunto, cuerpo, validarCodigoDTO.email());
            emailService.enviarCorreo(emailDTO);

            throw new CuentaException("El código de verificación ha expirado. Se ha enviado un nuevo código.");
        }

        // Comparar el código enviado con el código almacenado
        if (!cuenta.getCodigoVerificacionRegistro().equals(validarCodigoDTO.codigo())) {
            throw new CuentaException("El código de verificación es incorrecto.");
        }

        // Si el código es correcto y no ha expirado, activar la cuenta
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setCodigoVerificacionRegistro(null);
        cuenta.setFechaExpiracionCodigo(null);
        cuentaRepository.save(cuenta);

        cuponBienvenida(cuenta.getEmail());

        return validarCodigoDTO;
    }

    private void cuponBienvenida(String email){
        String codigoCupon = generarCodigoValidacion();
        String nombre = "Cupon de Bienvenida";
        float descuento = 15.5f;
        LocalDateTime fechaVencimiento = LocalDateTime.now().plusMonths(1);
        LocalDateTime fechaApertura = LocalDateTime.now();
        TipoCupon tipoCupon = TipoCupon.UNICO;
        Cupon nuevoCupon = new Cupon(nombre, codigoCupon,fechaVencimiento,fechaApertura,descuento,tipoCupon, EstadoCupon.DISPONIBLE, false);
        emailService.enviarCorreo(new EmailDTO("Bienvenido, gracias por confiar en nosotros", "Al registrarte tienes un código del 15% de descuento: "+ codigoCupon +" este código es redimible una vez en cualquier orden", email));
        cuponRepository.save(nuevoCupon);
    }

    // Metoodo para generar un código de validación de 5 cifras
    private String generarCodigoValidacion() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }

    //Metodo para encriptar la contraseña
    private String encriptarPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode( password );
    }

    /**
     * Metodo para editar los datos de una cuenta
     * param cuenta
     * return Mensaje de exito de que los fueros modificados
     * throws CuentaException
     */
    @Override
    public void editarCuenta(EditarCuentaDTO cuentaEditada) throws CuentaException {
        //Buscamos la cuenta del usuario que se quiere actualizar
        Optional<Cuenta> optionalCuenta = obtenerCuentaPorId(cuentaEditada.id());

        //Si no se encontró la cuenta del usuario, lanzamos una excepción
        if(optionalCuenta.isEmpty()){
            throw new CuentaException("No existe un usuario con el id dado");
        }

        //Obtenemos la cuenta del usuario a modificar y actualizamos sus datos
        Cuenta cuentaModificada = optionalCuenta.get();
        cuentaModificada.setNombre( cuentaEditada.nombre() );
        cuentaModificada.setTelefono( cuentaEditada.telefono() );
        // Actualizar la contraseña si se proporciona
        if (cuentaEditada.password() != null && !cuentaEditada.password().isEmpty()) {
            String encrptar = encriptarPassword(cuentaEditada.password());
            cuentaModificada.setPassword(encrptar);
        }

        //Como el objeto cuenta ya tiene un id, el save() no crea un nuevo registro sino que actualiza el que ya existe
        cuentaRepository.save(cuentaModificada);
    }

    /**
     * Metodo para eliminar una cuenta por su ID
     * param id
     * return el id de la cuenta eliminada
     * throws CuentaException
     */
    @Override
    public void eliminarCuenta(String id) throws CuentaException{
        //Buscamos la cuenta del usuario que se quiere eliminar
        Optional<Cuenta> optionalCuenta = obtenerCuentaPorId(id);

        //Si no se encontró la cuenta, lanzamos una excepción
        if(optionalCuenta.isEmpty()){
            throw new CuentaException("No se encontró el usuario con el id "+id);
        }

        //Obtenemos la cuenta del usuario que se quiere eliminar y le asignamos el estado eliminado
        Cuenta cuenta = optionalCuenta.get();
        cuenta.setEstado(EstadoCuenta.INACTIVO);

        cuentaRepository.save(cuenta);
    }

    /**
     * Metodo para obtener la informacion de una cuenta
     * param id
     * return los datos obtenidos de una cuenta por su id
     * throws CuentaException
     */
    @Override
    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws CuentaException {
        System.out.println("Buscando cuenta con ID: " + id);
        Optional<Cuenta> optionalCuenta = obtenerCuentaPorId(id);

        if (optionalCuenta.isEmpty()) {
            System.out.println("No se encontró la cuenta con ID: " + id);
            throw new CuentaException("No se encontró el usuario con el id " + id);
        }

        Cuenta cuenta = optionalCuenta.get();
        System.out.println("Cuenta encontrada: " + cuenta);

        return new InformacionCuentaDTO(
                cuenta.getId(),
                cuenta.getCedula(),
                cuenta.getNombre(),
                cuenta.getTelefono(),
                cuenta.getEmail()
        );
    }

    /**
     * Metodo para enviar los codigos de recuperacion para un contraseña
     * param correo
     * return
     * throws CuentaException
     */
    @Override
    public void enviarCodigoRecuperacionPassword(CodigoContraseniaDTO codigoContrasenieDTO) throws CuentaException {

        // Buscar la cuenta en la base de datos usando el email proporcionado
        Cuenta cuentaExistente = cuentaRepository.findByEmail(codigoContrasenieDTO.email())
                .orElseThrow(() -> new CuentaException("Cuenta no encontrada para el email proporcionado"));

        // Generar un código de recuperación único
        String codigoRecuperacion = generarCodigoValidacion();

        // Guardar el código en la cuenta (o en un lugar seguro relacionado con la cuenta)
        cuentaExistente.setCodigoVerificacionContrasenia(codigoRecuperacion);
        cuentaExistente.setFechaExpiracionCodigoContrasenia(LocalDateTime.now().plusMinutes(10));
        cuentaRepository.save(cuentaExistente);

        // Enviar el código de validación al correo
        String asunto = "Código de recuperación";
        String cuerpo = "El código para recuperar tu contraseña es: " + codigoRecuperacion;
        EmailDTO emailDTO = new EmailDTO(asunto, cuerpo, codigoContrasenieDTO.email());
        emailService.enviarCorreo(emailDTO);
    }

    @Override
    public void cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws CuentaException {
        // Buscar la cuenta usando el nuevo metodo
        Cuenta cuenta = cuentaRepository.findByCodigoVerificacionContrasenia(cambiarPasswordDTO.codigoVerificacion())
                .orElseThrow(() -> new CuentaException("Código de verificación inválido o expirado."));

        // Verificar si el código ha expirado
        if (cuenta.getFechaExpiracionCodigoContrasenia() != null && LocalDateTime.now().isAfter(cuenta.getFechaExpiracionCodigoContrasenia())) {
            // El código ha expirado, genera uno nuevo
            String nuevoCodigo = generarCodigoValidacion();
            cuenta.setCodigoVerificacionContrasenia(nuevoCodigo);
            cuenta.setFechaExpiracionCodigoContrasenia(LocalDateTime.now().plusMinutes(10)); // Expira en 10 minutos
            cuentaRepository.save(cuenta);

            // Enviar el nuevo código al correo
            String asunto = "Nuevo Código de Recuperación";
            String cuerpo = "Tu nuevo código de recuperación de contraseña es: " + nuevoCodigo;
            EmailDTO emailDTO = new EmailDTO(asunto, cuerpo, cuenta.getEmail());
            emailService.enviarCorreo(emailDTO);

            throw new CuentaException("El código de recuperación ha expirado. Se ha enviado un nuevo código.");
        }

        // Verificar que el código de verificación sea el mismo que el enviado
        if (!cuenta.getCodigoVerificacionContrasenia().equals(cambiarPasswordDTO.codigoVerificacion())) {
            throw new CuentaException("Código de verificación inválido.");
        }

        // Actualizar la contraseña con la nueva contraseña proporcionada
        cuenta.setPassword(encriptarPassword(cambiarPasswordDTO.passwordNueva()));

        // Limpiar el código de verificación y la fecha de expiración después de cambiar la contraseña
        cuenta.setCodigoVerificacionContrasenia(null);
        cuenta.setFechaExpiracionCodigoContrasenia(null);

        // Guardar los cambios en la base de datos
        cuentaRepository.save(cuenta);
    }

    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO)  throws CuentaException {
        // Buscar la cuenta por email electrónico
        Optional<Cuenta> optionalCuenta = cuentaRepository.findByEmail(loginDTO.email());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Si la cuenta no existe, lanzar excepción
        Cuenta cuenta = optionalCuenta.orElseThrow(() -> new CuentaException("Correo electrónico o contraseña incorrectos."));

        if( !passwordEncoder.matches(loginDTO.password(), cuenta.getPassword()) ) {
            throw new CuentaException("La contraseña es incorrecta");
        }

        // Verificar el estado de la cuenta (debe estar activa para iniciar sesión)
        if (!cuenta.getEstado().equals(EstadoCuenta.ACTIVO)) {
            throw new CuentaException("La cuenta no está activa.");
        }

        Map<String, Object> map = construirClaims(cuenta);
        return new TokenDTO( jwtUtils.generarToken(cuenta.getEmail(), map) );
    }

    @Override
    public Map<String, Object> construirClaims(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getNombre(),
                "id", cuenta.getId()
        );
    }
}