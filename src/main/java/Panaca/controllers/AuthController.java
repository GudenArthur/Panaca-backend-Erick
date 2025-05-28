package Panaca.controllers;

import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.autenticacion.TokenDTO;
import Panaca.dto.cuenta.*;
import Panaca.exceptions.CarritoException;
import Panaca.exceptions.CuentaException;
import Panaca.service.service.CuentaService;
import Panaca.service.service.CuponService;
import Panaca.service.service.EmailService;
import Panaca.service.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
    origins = {
        "http://localhost:4200",
        "https://panaca-front-erick.vercel.app"
    },
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowCredentials = "true"
)

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    CuentaService cuentaService;
    @Autowired
    EmailService emailService;
    @Autowired
    CuponService cuponService;
    @Autowired
    EventoService eventoService;

    /**
     * Metodo para crear la cuenta de un usuario
     * param cuentaDTO
     * return El DTO de la cuenta creada
     * throws CuentaException
     * throws CarritoException
     */
    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@Valid @RequestBody CrearCuentaDTO cuentaDTO) throws CuentaException, CarritoException {
        try {
            cuentaService.crearCuenta(cuentaDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cuenta creada correctamente"));
        } catch (CuentaException | CarritoException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @PostMapping("/validar-codigo-registro")
    public ResponseEntity<MensajeDTO<String>> validarCodigo(@Valid @RequestBody ValidarCodigoDTO validarCodigoDTO)throws CuentaException {
        try {
            cuentaService.validarCodigo(validarCodigoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cuenta activada con exito"));
        }catch (CuentaException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @PostMapping("/enviar-codigo-recuperacion-contasenia")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacion(@Valid @RequestBody CodigoContraseniaDTO codigoContraseniaDTO) throws CuentaException{
        try {
            cuentaService.enviarCodigoRecuperacionPassword(codigoContraseniaDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Código enviado correctamente al correo electrónico."));
        }catch (CuentaException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, "Error al enviar el correo"));
        }
    }

    /**
     * Metodo para modificar la contraseña, ya sea usuario o admin
     * param cambiarPasswordDTO
     * return El DTO con la password modificada
     * throws CuentaException
     */
    @PutMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO) throws CuentaException{
        try {
            cuentaService.cambiarPassword(cambiarPasswordDTO);
            return ResponseEntity.ok(new MensajeDTO<>(false, "Contraseña modificada con exito"));
        }catch (CuentaException e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS AUTENTICACION =============================================//

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception{
        TokenDTO token = cuentaService.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, token));
    }
}