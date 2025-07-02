package com.example.pokemonapi.service;

import com.example.pokemonapi.exception.PokemonNotFoundException;
import com.example.pokemonapi.model.Pokemon;
import com.example.pokemonapi.model.PokemonStatsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Servicio que encapsula la lógica de negocio para obtener y transformar datos de Pokémon.
 * Se comunica con la PokeAPI externa, maneja los errores y construye la respuesta simplificada.
 */
@Service
public class PokemonService {

    private final RestTemplate restTemplate;
    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    /**
     * Constructor para la inyección de dependencias de Spring.
     *
     * @param restTemplate el cliente HTTP para comunicarse con la PokeAPI.
     */
    @Autowired
    public PokemonService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Orquesta la obtención de estadísticas de un Pokémon por su nombre.
     *
     * @param pokemonName el nombre del Pokémon a buscar.
     * @return un DTO {@link PokemonStatsResponse} con los datos simplificados y en español.
     * @throws PokemonNotFoundException si el Pokémon no se encuentra en la PokeAPI.
     */
    public PokemonStatsResponse getPokemonStats(String pokemonName) {
        Pokemon pokemon = callPokeApi(pokemonName);
        return buildResponseFromPokemon(pokemon);
    }

    /**
     * Transforma un objeto {@link Pokemon} completo en un DTO {@link PokemonStatsResponse} simplificado.
     * Utiliza el patrón Builder para construir la respuesta paso a paso.
     *
     * @param pokemon el objeto Pokémon deserializado de la PokeAPI.
     * @return el DTO de respuesta simplificado.
     */
    private PokemonStatsResponse buildResponseFromPokemon(Pokemon pokemon) {
        PokemonStatsResponse.Builder builder = new PokemonStatsResponse.Builder()
                .nombre(pokemon.getName());

        toStream(pokemon.getStats()).forEach(statEntry -> {
            switch (statEntry.getStat().getName()) {
                case "hp": builder.hp(statEntry.getBaseStat()); break;
                case "attack": builder.ataque(statEntry.getBaseStat()); break;
                case "defense": builder.defensa(statEntry.getBaseStat()); break;
                case "speed": builder.velocidad(statEntry.getBaseStat()); break;
            }
        });

        toStream(pokemon.getTypes())
                .map(typeEntry -> typeEntry.getType().getName())
                .forEach(builder::addTipo);

        toStream(pokemon.getAbilities())
                .map(abilityEntry -> abilityEntry.getAbility().getName())
                .forEach(builder::addHabilidad);

        return builder.build();
    }

    /**
     * Realiza la llamada HTTP a la PokeAPI para obtener los datos de un Pokémon.
     *
     * @param pokemonName el nombre del Pokémon.
     * @return un objeto {@link Pokemon} con la respuesta deserializada.
     * @throws PokemonNotFoundException si la API devuelve un 404.
     * @throws RuntimeException para otros errores de comunicación.
     */
    private Pokemon callPokeApi(String pokemonName) {
        String url = POKEAPI_BASE_URL + pokemonName.toLowerCase();
        try {
            return restTemplate.getForObject(url, Pokemon.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new PokemonNotFoundException("Pokémon '" + pokemonName + "' no encontrado.");
            }
            throw new RuntimeException("Error al consumir la PokeAPI para '" + pokemonName + "': " + ex.getStatusCode(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado al consumir la PokeAPI para '" + pokemonName + "'", ex);
        }
    }

    /**
     * Método de utilidad para convertir una lista potencialmente nula en un Stream.
     * Esto evita {@link NullPointerException} y simplifica el código.
     *
     * @param list la lista a convertir.
     * @param <T> el tipo genérico de los elementos de la lista.
     * @return un Stream de los elementos de la lista, o un Stream vacío si la lista es nula.
     */
    private <T> Stream<T> toStream(List<T> list) {
        return Optional.ofNullable(list).stream().flatMap(Collection::stream);
    }
}
