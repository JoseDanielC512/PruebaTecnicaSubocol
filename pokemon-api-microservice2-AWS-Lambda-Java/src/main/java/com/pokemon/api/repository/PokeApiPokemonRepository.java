package com.pokemon.api.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pokemon.api.model.dto.Pokemon;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementación del Patrón Repositorio para obtener Pokémon desde la PokeAPI.
 * Encapsula la lógica de comunicación con la API externa.
 */
public class PokeApiPokemonRepository implements PokemonRepository {

    private static final String POKEAPI_BASE_URL = "https://pokeapi.co/api/v2/type/";
    private final CloseableHttpClient httpClient;
    private final Gson gson;

    /**
     * Constructor. Instancia el cliente HTTP y el parser GSON.
     * Estos objetos se instancian una sola vez para minimizar el impacto
     * del "cold start" en AWS Lambda.
     */
    public PokeApiPokemonRepository() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();
    }

    @Override
    public List<Pokemon> findByType(String type) throws Exception {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de Pokémon no puede ser nulo o vacío.");
        }

        String url = POKEAPI_BASE_URL + type.toLowerCase();
        HttpGet request = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();

            if (entity == null) {
                throw new IOException("La respuesta de la PokeAPI no contiene contenido.");
            }

            String jsonResponse = EntityUtils.toString(entity);

            if (statusCode == 404) {
                // Tipo de Pokémon no encontrado
                return Collections.emptyList();
            } else if (statusCode >= 200 && statusCode < 300) {
                // Éxito
                return parsePokemonList(jsonResponse);
            } else {
                // Otros errores HTTP
                throw new IOException("Error al llamar a PokeAPI. Código de estado: " + statusCode + ", Respuesta: " + jsonResponse);
            }
        } catch (IOException e) {
            // Captura errores de red o de comunicación
            throw new Exception("Error de comunicación con PokeAPI: " + e.getMessage(), e);
        }
    }

    /**
     * Parsea la respuesta JSON de la PokeAPI para extraer la lista de Pokémon.
     *
     * @param jsonResponse La cadena JSON recibida de la PokeAPI.
     * @return Una lista de objetos Pokemon.
     */
    private List<Pokemon> parsePokemonList(String jsonResponse) {
        List<Pokemon> pokemonList = new ArrayList<>();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

        if (jsonObject != null && jsonObject.has("pokemon")) {
            JsonArray pokemonArray = jsonObject.getAsJsonArray("pokemon");
            for (JsonElement element : pokemonArray) {
                JsonObject pokemonEntry = element.getAsJsonObject();
                if (pokemonEntry.has("pokemon")) {
                    JsonObject pokemonDetails = pokemonEntry.getAsJsonObject("pokemon");
                    String name = pokemonDetails.has("name") ? pokemonDetails.get("name").getAsString() : null;
                    String url = pokemonDetails.has("url") ? pokemonDetails.get("url").getAsString() : null;
                    if (name != null) {
                        pokemonList.add(new Pokemon(name, url));
                    }
                }
            }
        }
        return pokemonList;
    }
}
