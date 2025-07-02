package com.example.pokemonapi.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.pokemonapi.service.PokemonService;
import com.example.pokemonapi.model.PokemonStatsResponse;

/**
 * Controlador REST que expone el endpoint para obtener información de los Pokémon.
 * Gestiona las solicitudes HTTP y las delega al servicio PokemonService.
 */
@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    /**
     * Constructor que inyecta la dependencia de PokemonService.
     *
     * @param pokemonService el servicio que contiene la lógica de negocio para los Pokémon.
     */
    @Autowired
    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    /**
     * Endpoint para obtener las estadísticas simplificadas de un Pokémon por su nombre.
     * Responde a las solicitudes GET en /pokemon/{name}.
     *
     * @param name el nombre del Pokémon a buscar (pasado como parte de la URL).
     * @return un objeto {@link PokemonStatsResponse} con las estadísticas del Pokémon,
     *         que se serializa automáticamente a JSON.
     */
    @GetMapping("/{name}")
    public PokemonStatsResponse getPokemonStats(@PathVariable String name) {
        return pokemonService.getPokemonStats(name);
    }
}
