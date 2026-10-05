package com.example.productos_api.exception;

import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorGlobalExcepciones.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejadorValidacion(MethodArgumentNotValidException ex){

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        // MethodArgumentNotValidException (400)
        LOG.warn("Validación fallida: {}", ex.getBindingResult().getFieldErrors().stream().map(f -> f.getField() + ": " + f.getDefaultMessage()).toList());

        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejadorArgumentoInvalido(IllegalArgumentException ex) {

        // IllegalArgumentException (400)
        LOG.warn("Petición inválida: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarConflicto(DataIntegrityViolationException ex) {

        // DataIntegrityViolationException (409)
        LOG.warn("Conflicto de integridad de datos", ex);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "El recurso ya existe o viola una restricción de la base de datos"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarInesperado(Exception ex) {
        LOG.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> manejarRutaNoEncontrada(NoResourceFoundException ex) {
        LOG.warn("Ruta no encontrada: {}", ex.getResourcePath());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Recurso no encontrado"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException ex) {
        LOG.warn("Metodo no permitido: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of("error", "Metodo HTTP no permitido para esta ruta"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {

        LOG.warn("Cuerpo de la petición ilegible: {}", ex.getMessage());

        return ResponseEntity.badRequest()
                .body(Map.of("error", "El cuerpo de la petición no es un JSON válido"));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> manejarParametroFaltante(MissingServletRequestParameterException ex) {

        LOG.warn("Falta el parámetro obligatorio: {}", ex.getParameterName());

        return ResponseEntity.badRequest()
                .body(Map.of("error", "Falta el parámetro obligatorio: " + ex.getParameterName() ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {

        LOG.warn("Valor inválido para el parámetro '{}': {}", ex.getName(), ex.getValue());

        return ResponseEntity.badRequest()
                .body(Map.of("error", "Valor inválido para el parámetro: " + ex.getName()));
    }
}
