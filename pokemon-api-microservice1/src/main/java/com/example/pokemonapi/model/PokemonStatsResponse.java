package com.example.pokemonapi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO (Data Transfer Object) que representa la respuesta JSON simplificada.
 * Contiene solo las estadísticas principales de un Pokémon y se presenta en español.
 * Esta clase es inmutable y se construye utilizando el Patrón Builder.
 */
public class PokemonStatsResponse {
    private final String nombre;
    private final int hp;
    private final int ataque;
    private final int defensa;
    private final int velocidad;
    private final List<String> tipos;
    private final List<String> habilidades;

    /**
     * Constructor privado que se utiliza exclusivamente por la clase Builder interna.
     * @param builder El builder con los datos para construir el objeto.
     */
    private PokemonStatsResponse(Builder builder) {
        this.nombre = builder.nombre;
        this.hp = builder.hp;
        this.ataque = builder.ataque;
        this.defensa = builder.defensa;
        this.velocidad = builder.velocidad;
        this.tipos = builder.tipos;
        this.habilidades = builder.habilidades;
    }

    // Getters públicos
    public String getNombre() { return nombre; }
    public int getHp() { return hp; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getVelocidad() { return velocidad; }
    public List<String> getTipos() { return tipos; }
    public List<String> getHabilidades() { return habilidades; }

    /**
     * Clase estática anidada que implementa el Patrón Builder para construir
     * un objeto {@link PokemonStatsResponse} de forma segura y paso a paso.
     */
    public static class Builder {
        private String nombre;
        private int hp;
        private int ataque;
        private int defensa;
        private int velocidad;
        private List<String> tipos = new ArrayList<>();
        private List<String> habilidades = new ArrayList<>();

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder hp(int hp) {
            this.hp = hp;
            return this;
        }

        public Builder ataque(int ataque) {
            this.ataque = ataque;
            return this;
        }

        public Builder defensa(int defensa) {
            this.defensa = defensa;
            return this;
        }

        public Builder velocidad(int velocidad) {
            this.velocidad = velocidad;
            return this;
        }

        public Builder addTipo(String tipo) {
            this.tipos.add(tipo);
            return this;
        }

        public Builder addHabilidad(String habilidad) {
            this.habilidades.add(habilidad);
            return this;
        }

        /**
         * Construye y devuelve el objeto {@link PokemonStatsResponse} inmutable.
         * @return una nueva instancia de PokemonStatsResponse.
         */
        public PokemonStatsResponse build() {
            return new PokemonStatsResponse(this);
        }
    }
}
