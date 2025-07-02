package com.example.pokemonapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Representa la estructura completa de un Pokémon deserializada desde la PokeAPI.
 * Esta clase actúa como un POJO (Plain Old Java Object) para que Jackson mapee la
 * respuesta JSON de la API a un objeto Java. Solo se incluyen los campos relevantes
 * para esta aplicación.
 */
public class Pokemon {

    /**
     * El nombre del Pokémon.
     */
    private String name;

    /**
     * Una lista de las estadísticas base del Pokémon (HP, Ataque, Defensa, etc.).
     * Mapeado desde la clave "stats" en el JSON.
     */
    private List<PokemonStat> stats;

    /**
     * Una lista de los tipos a los que pertenece el Pokémon (e.g., "electric").
     * Mapeado desde la clave "types" en el JSON.
     */
    private List<PokemonType> types;

    /**
     * Una lista de las habilidades que el Pokémon puede tener.
     * Mapeado desde la clave "abilities" en el JSON.
     */
    private List<PokemonAbility> abilities;

    // Getters y Setters necesarios para que Jackson pueda acceder a los campos.
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<PokemonStat> getStats() { return stats; }
    public void setStats(List<PokemonStat> stats) { this.stats = stats; }

    public List<PokemonType> getTypes() { return types; }
    public void setTypes(List<PokemonType> types) { this.types = types; }

    public List<PokemonAbility> getAbilities() { return abilities; }
    public void setAbilities(List<PokemonAbility> abilities) { this.abilities = abilities; }


    // --- Clases Estáticas Anidadas para Deserialización ---

    /**
     * Representa una entrada de estadística individual dentro de la lista "stats".
     */
    public static class PokemonStat {
        @JsonProperty("base_stat")
        private int baseStat;
        private NamedApiResource stat;

        public int getBaseStat() { return baseStat; }
        public void setBaseStat(int baseStat) { this.baseStat = baseStat; }

        public NamedApiResource getStat() { return stat; }
        public void setStat(NamedApiResource stat) { this.stat = stat; }
    }

    /**
     * Representa una entrada de tipo individual dentro de la lista "types".
     */
    public static class PokemonType {
        private NamedApiResource type;

        public NamedApiResource getType() { return type; }
        public void setType(NamedApiResource type) { this.type = type; }
    }

    /**
     * Representa una entrada de habilidad individual dentro de la lista "abilities".
     */
    public static class PokemonAbility {
        private NamedApiResource ability;

        public NamedApiResource getAbility() { return ability; }
        public void setAbility(NamedApiResource ability) { this.ability = ability; }
    }

    /**
     * Representa un recurso con nombre genérico dentro de la PokeAPI (e.g., un tipo,
     * una habilidad, o el nombre de una estadística). Contiene el nombre del recurso.
     */
    public static class NamedApiResource {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
} 