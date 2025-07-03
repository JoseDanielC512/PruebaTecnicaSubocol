package com.pokemon.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.pokemon.api.model.dto.Pokemon;
import com.pokemon.api.repository.PokeApiPokemonRepository;
import com.pokemon.api.repository.PokemonRepository;
import com.pokemon.api.util.ApiResponse;

import java.util.List;
import java.util.Map;

/**
 * Clase principal de la función AWS Lambda para obtener Pokémon por tipo.
 * Implementa el Patrón Repositorio para desacoplar la lógica de negocio
 * de la capa de acceso a datos (PokeAPI).
 */
public class Handler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    // Instancia del repositorio. Se inicializa una vez para reutilizar en "cold starts".
    private final PokemonRepository pokemonRepository;

    /**
     * Constructor por defecto.
     * Se utiliza para la inyección de dependencias simple (en este caso, el repositorio).
     */
    public Handler() {
        this.pokemonRepository = new PokeApiPokemonRepository();
    }

    /**
     * Constructor para inyección de dependencias en pruebas.
     *
     * @param pokemonRepository La implementación del repositorio a usar.
     */
    public Handler(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        context.getLogger().log("Received request: " + request.toString());

        // 1. Extraer el tipo de Pokémon de los pathParameters
        Map<String, String> pathParameters = request.getPathParameters();
        String pokemonType = null;
        if (pathParameters != null && pathParameters.containsKey("type")) {
            pokemonType = pathParameters.get("type");
        }

        if (pokemonType == null || pokemonType.trim().isEmpty()) {
            context.getLogger().log("Error: Tipo de Pokémon no proporcionado o vacío.");
            return ApiResponse.error(400, "El tipo de Pokémon es un parámetro requerido.");
        }

        try {
            // 2. Consumir la PokeAPI a través del repositorio
            List<Pokemon> pokemons = pokemonRepository.findByType(pokemonType);

            if (pokemons.isEmpty()) {
                context.getLogger().log("No se encontraron Pokémon para el tipo: " + pokemonType);
                // Podría ser 404 si el tipo no existe o 200 con lista vacía si el tipo existe pero no tiene pokemons
                // La implementación del repositorio ya maneja 404 de la PokeAPI devolviendo lista vacía.
                // Decidimos devolver 200 OK con lista vacía para un tipo válido sin pokemons, o 404 si el tipo no existe.
                // Para este caso, si la lista es vacía, asumimos que el tipo no produjo resultados.
                return ApiResponse.error(404, "No se encontraron Pokémon para el tipo: " + pokemonType);
            }

            context.getLogger().log("Found " + pokemons.size() + " pokemons for type: " + pokemonType);
            // 3. Retornar la lista de Pokémon
            return ApiResponse.success(pokemons);

        } catch (IllegalArgumentException e) {
            context.getLogger().log("Bad Request Error: " + e.getMessage());
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            context.getLogger().log("Internal Server Error: " + e.getMessage());
            // Para errores inesperados o de comunicación con la PokeAPI
            return ApiResponse.error(500, "Error interno del servidor al obtener Pokémon: " + e.getMessage());
        }
    }
}
