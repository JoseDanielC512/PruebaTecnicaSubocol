package com.example.pokemonapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de integración que verifica si el contexto de la aplicación Spring Boot se carga correctamente.
 * Anotar con {@link SpringBootTest} le dice a Spring Boot que busque una configuración principal
 * (una con {@code @SpringBootApplication}) y la use para iniciar un contexto de aplicación de Spring.
 */
@SpringBootTest
class PokemonApiApplicationTests {

	/**
	 * Esta prueba, aunque vacía, confirma que la aplicación puede arrancar sin errores.
	 * Si todas las dependencias se inyectan y los beans se configuran correctamente,
	 * el contexto se cargará y la prueba pasará.
	 */
	@Test
	void contextLoads() {
	}

}
