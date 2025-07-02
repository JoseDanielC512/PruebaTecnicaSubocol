package com.example.pokemonapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador de excepciones global para toda la aplicación.
 * Utiliza @ControllerAdvice para interceptar excepciones lanzadas por los controladores
 * y devolver respuestas HTTP estandarizadas y consistentes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Formato de fecha y hora para las respuestas
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Maneja las excepciones de tipo {@link PokemonNotFoundException}.
     * Se activa cuando un Pokémon solicitado no se encuentra en la PokeAPI.
     *
     * @param ex la excepción {@link PokemonNotFoundException} lanzada.
     * @param request el objeto WebRequest asociado a la solicitud actual.
     * @return un {@link ResponseEntity} con un cuerpo de error detallado y un estado HTTP 404 (Not Found).
     */
    @ExceptionHandler(PokemonNotFoundException.class)
    public ResponseEntity<Object> handlePokemonNotFoundException(
            PokemonNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fecha", dtf.format(LocalDateTime.now()));
        body.put("estado", HttpStatus.NOT_FOUND.value());
        body.put("error", "Pokemon no encontrado");
        body.put("mensaje", ex.getMessage());
        body.put("ruta", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Manejador genérico para todas las demás excepciones no capturadas.
     * Actúa como una red de seguridad para evitar que errores inesperados expongan
     * detalles de la implementación.
     *
     * @param ex la excepción genérica lanzada.
     * @param request el objeto WebRequest asociado a la solicitud actual.
     * @return un {@link ResponseEntity} con un mensaje de error genérico y un estado HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fecha", dtf.format(LocalDateTime.now()));
        body.put("estado", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error Interno del Servidor");
        body.put("mensaje", "Ocurrió un error inesperado: " + ex.getMessage());
        body.put("ruta", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
