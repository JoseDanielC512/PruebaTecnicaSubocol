package com.example.pokemonapi.controller;

import com.example.pokemonapi.exception.PokemonNotFoundException;
import com.example.pokemonapi.model.PokemonStatsResponse;
import com.example.pokemonapi.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para la clase {@link PokemonController}.
 * Se utiliza {@link WebMvcTest} para probar la capa web sin levantar un servidor completo.
 * {@link MockMvc} se usa para realizar solicitudes HTTP simuladas y verificar las respuestas.
 */
@WebMvcTest(PokemonController.class)
public class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Se crea un mock de {@link PokemonService} para aislar al controlador
     * y simular su comportamiento durante las pruebas.
     */
    @MockBean
    private PokemonService pokemonService;

    /**
     * Prueba el caso de éxito donde se encuentra un Pokémon.
     * Verifica que el endpoint devuelve un estado HTTP 200 (OK) y que el cuerpo JSON
     * contiene los datos correctos y en español.
     * @throws Exception si ocurre un error durante la ejecución de MockMvc.
     */
    @Test
    void getPokemonStats_shouldReturnPokemonStats_whenPokemonFound() throws Exception {
        // 1. Arrange: Configurar el mock del servicio
        PokemonStatsResponse mockResponse = new PokemonStatsResponse.Builder()
                .nombre("pikachu")
                .hp(35)
                .ataque(55)
                .defensa(40)
                .velocidad(90)
                .addTipo("electric")
                .addHabilidad("static")
                .addHabilidad("lightning-rod")
                .build();

        when(pokemonService.getPokemonStats(anyString())).thenReturn(mockResponse);

        // 2. Act & 3. Assert: Ejecutar la solicitud y verificar el resultado
        mockMvc.perform(get("/pokemon/pikachu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("pikachu"))
                .andExpect(jsonPath("$.hp").value(35))
                .andExpect(jsonPath("$.ataque").value(55))
                .andExpect(jsonPath("$.defensa").value(40))
                .andExpect(jsonPath("$.velocidad").value(90))
                .andExpect(jsonPath("$.tipos[0]").value("electric"))
                .andExpect(jsonPath("$.habilidades[0]").value("static"))
                .andExpect(jsonPath("$.habilidades[1]").value("lightning-rod"));
    }

    /**
     * Prueba el caso en que el Pokémon solicitado no existe.
     * Verifica que el endpoint devuelve un estado HTTP 404 (Not Found) cuando
     * el servicio lanza una {@link PokemonNotFoundException}.
     * @throws Exception si ocurre un error durante la ejecución de MockMvc.
     */
    @Test
    void getPokemonStats_shouldReturnNotFound_whenPokemonNotFound() throws Exception {
        // 1. Arrange: Configurar el mock para que lance una excepción
        when(pokemonService.getPokemonStats(anyString())).thenThrow(new PokemonNotFoundException("Pokemon not found"));

        // 2. Act & 3. Assert: Ejecutar y verificar que el estado es 404
        mockMvc.perform(get("/pokemon/nonexistent"))
                .andExpect(status().isNotFound());
    }
}
