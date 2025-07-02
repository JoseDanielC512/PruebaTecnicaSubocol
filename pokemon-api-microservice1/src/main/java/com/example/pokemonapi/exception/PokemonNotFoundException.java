package com.example.pokemonapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada que se lanza cuando un Pokémon solicitado no se encuentra.
 *
 * Al ser una {@link RuntimeException}, no necesita ser declarada explícitamente en las
 * firmas de los métodos. Es capturada por el {@link GlobalExceptionHandler} para
 * generar una respuesta HTTP 404 (Not Found).
 *
 * La anotación {@code @ResponseStatus(HttpStatus.NOT_FOUND)} sirve como un mecanismo
 * de respaldo para que Spring devuelva un 404 automáticamente si no hubiera un
 * @ExceptionHandler específico.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PokemonNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con el mensaje de detalle especificado.
     *
     * @param message el mensaje de detalle.
     */
    public PokemonNotFoundException(String message) {
        super(message);
    }
}
