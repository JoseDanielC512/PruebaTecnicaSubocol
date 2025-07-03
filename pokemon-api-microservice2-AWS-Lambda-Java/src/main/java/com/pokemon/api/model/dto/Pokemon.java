package com.pokemon.api.model.dto;

/**
 * DTO (Data Transfer Object) para representar un Pokémon.
 * Esta clase se utiliza para estructurar la respuesta de la API.
 */
public class Pokemon {
    private String name;
    private String url;

    public Pokemon() {
        // Constructor por defecto para deserialización de JSON
    }

    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
               "name='" + name + '\'' +
               ", url='" + url + '\'' +
               '}';
    }
}
