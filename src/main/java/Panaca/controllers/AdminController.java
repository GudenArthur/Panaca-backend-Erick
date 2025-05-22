package Panaca.controllers;

import Panaca.dto.PQR.InformacionPQRDTO;
import Panaca.dto.PQR.ResponderPQRDTO;
import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.cupon.*;
import Panaca.dto.devolucion.DevolucionResponseDTO;
import Panaca.dto.evento.CrearEventoDTO;
import Panaca.dto.evento.EditarEventoDTO;
import Panaca.exceptions.CuponException;
import Panaca.exceptions.EventoException;
import Panaca.exceptions.PQRException;
import Panaca.model.documents.Cupon;
import Panaca.service.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    EventoService eventoService;
    @Autowired
    ImagesService imagesService;
    @Autowired
    CuponService cuponService;
    @Autowired
    PQRService PQRService;
    @Autowired
    DevolucionService devolucionService;

    //==================================== METODOS EVENTO =============================================//
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear-evento")
    public ResponseEntity<MensajeDTO<String>> crearEvento(@Valid @RequestBody CrearEventoDTO eventoDTO) throws EventoException {
        try {
            eventoService.crearEvento(eventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento creado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editar-evento/{id}")
    public ResponseEntity<MensajeDTO<String>> editarEvento(@PathVariable("id") String id, @Valid @RequestBody EditarEventoDTO eventoDTO) throws EventoException {
        try{
            eventoService.editarEvento(id,eventoDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento modificado exitosamente"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
            }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(@Valid @PathVariable String id) throws EventoException {
        try{
            eventoService.eliminarEvento(id);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Evento eliminado exitosamente"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS PQR =============================================//

    /**
     * Método para responder una PQR.
     * @param responderPQRDTO Datos de respuesta a la PQR.
     * @return Mensaje de éxito.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/responder-pqr")
    public ResponseEntity<MensajeDTO<String>> responderPQR(@Valid @RequestBody ResponderPQRDTO responderPQRDTO) {
        try {
            PQRService.responderPQR(responderPQRDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true,"PQR respondida exitosamente"));
        } catch (PQRException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false,e.getMessage()));
        }
    }

    /**
     * Método para obtener información de una PQR por su ID.
     * @param id ID de la PQR.
     * @return Información de la PQR.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/obtener-informacion-pqr/{id}")
    public ResponseEntity<MensajeDTO<InformacionPQRDTO>> obtenerInformacionPQR(@Valid @PathVariable String id) throws PQRException {
        InformacionPQRDTO informacionPQRDTO = PQRService.obtenerInformacionPQR(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, informacionPQRDTO));
    }

    /**
     * Método para eliminar una PQR por su ID.
     * @param id ID de la PQR a eliminar.
     * @return Mensaje de éxito.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-pqr/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarPQR(@Valid @PathVariable String id) {
        try {
            PQRService.eliminarPQR(id);
            return ResponseEntity.ok(new MensajeDTO<>(true,"PQR eliminado con exito"));
        } catch (PQRException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false,e.getMessage()));
        }
    }

    //====================================== METODOS CUPON ====================================//

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear-cupon")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws CuponException {
        try {
            cuponService.crearCupon(cuponDTO);
            return ResponseEntity.ok(new MensajeDTO<>(true,"Cupon creado con exito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false,e.getMessage()));
            }
        }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editar-cupon/{cuponId}")
    public ResponseEntity<MensajeDTO<String>> editarCupon(@Valid @RequestBody EditarCuponDTO cupon, @PathVariable String cuponId) throws CuponException {
        try {
            cuponService.editarCupon(cupon, cuponId);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cupón editado con éxito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar-cupon/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String id) throws CuponException {
        try {
            cuponService.eliminarCupon(id);
            return ResponseEntity.ok(new MensajeDTO<>(true, "Cupón eliminado con éxito"));
        } catch (CuponException e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cupon/{id}")
    public ResponseEntity<InformacionCuponDTO> obtenerInformacionCupon(@PathVariable String id) throws CuponException {
        InformacionCuponDTO cuponInfo = cuponService.obtenerInformacionCupon(id);
        return ResponseEntity.ok(cuponInfo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cupones?estado=DISPONIBLE")
    public ResponseEntity<Page<Cupon>> getAllDisponibles(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "4") int size){
        PageRequest pageRequest= PageRequest.of(page, size);
        Page<Cupon> cupones = cuponService.getAllDisponibles(pageRequest);
        return ResponseEntity.ok(cupones);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cupones?estado=NO_DISPONIBLE")
    public ResponseEntity<Page<Cupon>> getAllNoDisponibles(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "4") int size){
        PageRequest pageRequest= PageRequest.of(page, size);
        Page<Cupon> cupones = cuponService.getAllNoDisponibles(pageRequest);
        return ResponseEntity.ok(cupones);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filtrar-cupones")
    public ResponseEntity<List<ItemsCuponDTO>> obtenerCuponesFiltrados(@RequestBody ItemsCuponFiltroDTO filtro) {
        List<ItemsCuponDTO> cuponesFiltrados = cuponService.obtenerCuponesFiltrados(filtro);
        return ResponseEntity.ok(cuponesFiltrados);
    }

    //=========================================== METODOS IMAGENES ===============================================//
/*
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/subir")
    public ResponseEntity<MensajeDTO<String>> subir(@RequestParam("imagen") MultipartFile imagen) throws Exception {
        String respuesta = imagesService.subirImagen(imagen);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, respuesta));
    }
*/
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{idImagen}")
    public ResponseEntity<MensajeDTO<String>> eliminar(@PathVariable String idImagen)  throws Exception{
        try {
            imagesService.eliminarImagen( idImagen );
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "La imagen fue eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MensajeDTO<>(false, e.getMessage()));
        }
    }

    //==================================== METODOS DEVOLUCIONES =============================================//

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/devoluciones")
    public ResponseEntity<MensajeDTO<List<DevolucionResponseDTO>>> listarTodasDevoluciones() {
        List<DevolucionResponseDTO> devoluciones = devolucionService.listarTodas();
        return ResponseEntity.ok(new MensajeDTO<>(true, devoluciones));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/devoluciones-aprobar/{id}")
    public ResponseEntity<MensajeDTO<DevolucionResponseDTO>> aprobarDevolucion(@PathVariable String id) {
        DevolucionResponseDTO dto = devolucionService.aprobar(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/devoluciones-rechazar/{id}")
    public ResponseEntity<MensajeDTO<DevolucionResponseDTO>> rechazarDevolucion(@PathVariable String id) {
        DevolucionResponseDTO dto = devolucionService.rechazar(id);
        return ResponseEntity.ok(new MensajeDTO<>(true, dto));
    }

}