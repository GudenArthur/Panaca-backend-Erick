package Panaca.exceptions;

import Panaca.dto.autenticacion.MensajeDTO;
import Panaca.dto.autenticacion.ValidacionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExcepcionesGlobales {

    @ExceptionHandler(CuentaException.class)
    public ResponseEntity<MensajeDTO<String>> handleCuentaException(CuentaException ex) {
        return ResponseEntity.badRequest().body(new MensajeDTO<>(true, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeDTO<String>> generalException(Exception e){
        return ResponseEntity.internalServerError().body( new MensajeDTO<>(true, e.getMessage()) );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeDTO<List<ValidacionDTO>>> validationException(MethodArgumentNotValidException ex ) {
        List<ValidacionDTO> errores = new ArrayList<>();
        BindingResult results = ex.getBindingResult();


        for (FieldError e: results.getFieldErrors()) {
            errores.add( new ValidacionDTO(e.getField(), e.getDefaultMessage()) );
        }

        return ResponseEntity.badRequest().body( new MensajeDTO<>(true, errores) );
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFound(FileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}