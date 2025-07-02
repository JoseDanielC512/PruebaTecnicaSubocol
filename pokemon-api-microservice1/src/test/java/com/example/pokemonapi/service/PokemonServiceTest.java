package com.example.pokemonapi.service;

import com.example.pokemonapi.exception.PokemonNotFoundException;
import com.example.pokemonapi.model.Pokemon;
import com.example.pokemonapi.model.PokemonStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para la clase {@link PokemonService}.
 * Se utiliza Mockito para simular las dependencias, como {@link RestTemplate},
 * y probar la lógica de negocio de forma aislada.
 */
public class PokemonServiceTest {

    /**
     * Mock de RestTemplate para simular las llamadas a la API externa
     * sin realizar solicitudes HTTP reales.
     */
    @Mock
    private RestTemplate restTemplate;

    /**
     * Inyecta los mocks (RestTemplate) en una instancia real de PokemonService
     * para poder probar sus métodos.
     */
    @InjectMocks
    private PokemonService pokemonService;

    /**
     * Inicializa los mocks de Mockito antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Prueba el flujo de éxito del método getPokemonStats.
     * Verifica que cuando la PokeAPI devuelve un Pokémon válido, el servicio
     * lo transforma correctamente en un {@link PokemonStatsResponse} con los
     * campos esperados y en español.
     */
    @Test
    void getPokemonStats_shouldReturnCorrectStats_whenPokemonFound() {
        // 1. Arrange: Crear un objeto Pokemon simulado, tal como lo devolvería la API
        Pokemon mockPokemon = createMockPokemon();

        when(restTemplate.getForObject(anyString(), eq(Pokemon.class)))
                .thenReturn(mockPokemon);

        // 2. Act: Llamar al método del servicio que se está probando
        PokemonStatsResponse result = pokemonService.getPokemonStats("pikachu");

        // 3. Assert: Verificar que el resultado es el esperado
        assertNotNull(result);
        assertEquals("pikachu", result.getNombre());
        assertEquals(35, result.getHp());
        assertEquals(55, result.getAtaque());
        assertEquals(40, result.getDefensa());
        assertEquals(90, result.getVelocidad());
        assertEquals(1, result.getTipos().size());
        assertTrue(result.getTipos().contains("electric"));
        assertEquals(2, result.getHabilidades().size());
        assertTrue(result.getHabilidades().contains("static"));
        assertTrue(result.getHabilidades().contains("lightning-rod"));
    }

    /**
     * Prueba el caso en que la PokeAPI no encuentra el Pokémon solicitado.
     * Verifica que el servicio captura la {@link HttpClientErrorException} con estado 404
     * y la convierte en nuestra excepción de dominio {@link PokemonNotFoundException}.
     */
    @Test
    void getPokemonStats_shouldThrowPokemonNotFoundException_whenPokemonNotFound() {
        // Arrange: Simular que RestTemplate lanza una excepción de cliente con estado 404
        when(restTemplate.getForObject(anyString(), eq(Pokemon.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Act & Assert: Verificar que se lanza la excepción correcta
        PokemonNotFoundException thrown = assertThrows(PokemonNotFoundException.class, () ->
                pokemonService.getPokemonStats("nonexistent")
        );

        assertTrue(thrown.getMessage().contains("no encontrado"));
    }

    /**
     * Prueba el caso de un error inesperado durante la llamada a la PokeAPI.
     * Verifica que el servicio captura una excepción genérica y la envuelve
     * en una {@link RuntimeException} para evitar exponer detalles de la implementación.
     */
    @Test
    void getPokemonStats_shouldThrowRuntimeException_whenApiCallFails() {
        // Arrange: Simular un error genérico en la llamada
        when(restTemplate.getForObject(anyString(), eq(Pokemon.class)))
                .thenThrow(new RuntimeException("API error"));

        // Act & Assert: Verificar que se lanza una RuntimeException con el mensaje esperado
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                pokemonService.getPokemonStats("pikachu")
        );

        assertTrue(thrown.getMessage().contains("Error inesperado al consumir la PokeAPI"));
    }

    /**
     * Método de utilidad para crear un objeto {@link Pokemon} complejo para las pruebas.
     * Esto mantiene los métodos de prueba limpios y centrados en su lógica.
     *
     * @return un objeto Pokemon completamente poblado.
     */
    private Pokemon createMockPokemon() {
        Pokemon mockPokemon = new Pokemon();
        mockPokemon.setName("pikachu");

        Pokemon.PokemonStat hpStat = new Pokemon.PokemonStat();
        hpStat.setBaseStat(35);
        hpStat.setStat(new Pokemon.NamedApiResource() {{ setName("hp"); }});

        Pokemon.PokemonStat attackStat = new Pokemon.PokemonStat();
        attackStat.setBaseStat(55);
        attackStat.setStat(new Pokemon.NamedApiResource() {{ setName("attack"); }});

        Pokemon.PokemonStat defenseStat = new Pokemon.PokemonStat();
        defenseStat.setBaseStat(40);
        defenseStat.setStat(new Pokemon.NamedApiResource() {{ setName("defense"); }});

        Pokemon.PokemonStat speedStat = new Pokemon.PokemonStat();
        speedStat.setBaseStat(90);
        speedStat.setStat(new Pokemon.NamedApiResource() {{ setName("speed"); }});

        mockPokemon.setStats(Arrays.asList(hpStat, attackStat, defenseStat, speedStat));

        Pokemon.PokemonType electricType = new Pokemon.PokemonType();
        electricType.setType(new Pokemon.NamedApiResource() {{ setName("electric"); }});
        mockPokemon.setTypes(Collections.singletonList(electricType));

        Pokemon.PokemonAbility staticAbility = new Pokemon.PokemonAbility();
        staticAbility.setAbility(new Pokemon.NamedApiResource() {{ setName("static"); }});
        Pokemon.PokemonAbility lightningRodAbility = new Pokemon.PokemonAbility();
        lightningRodAbility.setAbility(new Pokemon.NamedApiResource() {{ setName("lightning-rod"); }});
        mockPokemon.setAbilities(Arrays.asList(staticAbility, lightningRodAbility));

        return mockPokemon;
    }
}
