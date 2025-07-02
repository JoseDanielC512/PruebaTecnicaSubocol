package com.example.pokemonapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Clase de configuración de la aplicación que define los beans necesarios para el
 * contenedor de Spring.
 */
@Configuration
public class AppConfig {

    /**
     * Crea y configura un bean de RestTemplate para realizar solicitudes HTTP a APIs externas.
     * Este bean se inyecta en otras partes de la aplicación, como en PokemonService, para
     * consumir la PokeAPI.
     *
     * @return una nueva instancia de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
