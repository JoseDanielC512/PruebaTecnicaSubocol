package com.pokemon.api.repository;

import com.pokemon.api.model.dto.Pokemon;

import java.util.List;

/**
 * Interfaz del Patrón Repositorio para la gestión de Pokémon.
 * Define el contrato para la obtención de datos de Pokémon.
 */
public interface PokemonRepository {

    /**
     * Busca una lista de Pokémon por su tipo.
     *
     * @param type El tipo de Pokémon (ej. "fire", "water").
     * @return Una lista de objetos Pokemon que pertenecen al tipo especificado.
     *         Retorna una lista vacía si no se encuentran Pokémon para ese tipo
     *         o si el tipo no es válido.
     * @throws Exception Si ocurre un error durante la comunicación con la fuente de datos.
     */
    List<Pokemon> findByType(String type) throws Exception;
}
