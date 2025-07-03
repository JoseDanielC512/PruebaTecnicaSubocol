# Microservicio 2: Pokémon Type Lambda

Este microservicio es una función AWS Lambda desarrollada en Java que permite obtener una lista de Pokémon por su tipo, consumiendo la PokeAPI. La funcionalidad se expone a través de AWS API Gateway.

## Arquitectura y Patrones de Diseño

### Patrón de Diseño Principal: Repository Pattern (Patrón Repositorio)

*   **Ubicación:** Implementado a través de la interfaz `com.pokemon.api.repository.PokemonRepository` y su implementación `com.pokemon.api.repository.PokeApiPokemonRepository`.
*   **Propósito:** Este patrón se eligió para abstraer la lógica de acceso a datos de la PokeAPI del `Handler` principal de la Lambda.
    *   **Separación de Responsabilidades:** El `Handler` (`com.pokemon.api.Handler`) se enfoca en la orquestación de la solicitud (recibir el evento de API Gateway, extraer parámetros, formatear la respuesta), mientras que el `PokeApiPokemonRepository` se encarga exclusivamente de la comunicación con la PokeAPI, el manejo de las respuestas HTTP y el parseo del JSON.
    *   **Testeabilidad:** Permite probar la lógica del `Handler` de forma aislada, inyectando una implementación "mock" o falsa de `PokemonRepository` sin necesidad de realizar llamadas reales a la PokeAPI durante las pruebas unitarias. Esto acelera el desarrollo y mejora la fiabilidad de las pruebas.
    *   **Flexibilidad:** Si en el futuro la fuente de datos de Pokémon cambiara (ej., a una base de datos interna o a otra API), solo sería necesario crear una nueva implementación de `PokemonRepository` sin modificar el `Handler`.

### Patrón de Diseño Adicional: Data Transfer Object (DTO)

*   **Ubicación:** La clase `com.pokemon.api.model.dto.Pokemon`.
*   **Propósito:** El DTO se utiliza para definir la estructura de los datos que se transfieren entre las capas de la aplicación (en este caso, desde el repositorio hacia el `Handler` y finalmente como respuesta JSON de la API).
    *   **Desacoplamiento:** Asegura que la estructura de la respuesta de nuestra API no esté directamente acoplada a la estructura de la respuesta de la PokeAPI, permitiendo que ambas evolucionen de forma independiente.
    *   **Claridad:** Proporciona un contrato claro sobre qué información de Pokémon se está exponiendo.

## Estructura del Proyecto

```
.
├── pom.xml
├── README.md
└── src
    └── main
        └── java
            └── com
                └── pokemon
                    └── api
                        ├── Handler.java
                        ├── model
                        │   └── dto
                        │       └── Pokemon.java
                        ├── repository
                        │   ├── PokeApiPokemonRepository.java
                        │   └── PokemonRepository.java
                        └── util
                            └── ApiResponse.java
```

## Configuración de AWS Lambda y API Gateway

### AWS Lambda

*   **Runtime:** Java 11 (o superior, según la configuración del `pom.xml`).
*   **Handler:** `com.pokemon.api.Handler::handleRequest`
*   **Memoria:** Se recomienda un mínimo de 128MB, ajustando según el rendimiento.
*   **Timeout:** Configurar un tiempo de espera adecuado (ej., 10-30 segundos) para permitir la comunicación con la PokeAPI.
*   **Paquete de Despliegue:** El "uber JAR" generado por el `maven-shade-plugin` (ubicado en `target/pokemon-type-lambda-1.0.0.jar` después de `mvn clean package`).

### API Gateway

*   **Tipo de API:** REST API.
*   **Recurso:** `/pokemons/type/{type}`
    *   Método: `GET`
    *   Integración: `Lambda Function`
    *   Función Lambda: Seleccionar la función Lambda creada.
    *   **Mapeo de Parámetros:** Asegurar que el parámetro `{type}` de la ruta de API Gateway se mapee correctamente al evento de entrada de la Lambda (`event.getPathParameters().get("type")`).

## Manejo de Errores

El microservicio implementa un manejo de errores robusto para proporcionar respuestas claras al cliente:

*   **400 Bad Request:**
    *   Si el parámetro `{type}` no se proporciona o está vacío en la URL.
    *   Manejado por `IllegalArgumentException` en el `Handler`.
*   **404 Not Found:**
    *   Si la PokeAPI devuelve un 404 para un tipo de Pokémon no existente.
    *   Manejado por `PokeApiPokemonRepository` que retorna una lista vacía, y el `Handler` lo interpreta como 404.
*   **500 Internal Server Error:**
    *   Para errores inesperados durante la ejecución de la Lambda o problemas de comunicación con la PokeAPI.
    *   Capturado por un bloque `catch (Exception e)` general en el `Handler`.

La clase `com.pokemon.api.util.ApiResponse` se utiliza para estandarizar la creación de estas respuestas HTTP, incluyendo los encabezados `Content-Type: application/json` y `Access-Control-Allow-Origin: *` para CORS.

## Optimización

*   **Minimización de Cold Starts:** Las instancias de `CloseableHttpClient` y `Gson` se inicializan una sola vez en el constructor de `PokeApiPokemonRepository` y `ApiResponse` (estáticamente), respectivamente. Esto permite su reutilización en invocaciones posteriores de la misma instancia de Lambda, reduciendo el tiempo de arranque en frío.
*   **Maven Shade Plugin:** Utilizado para crear un único JAR con todas las dependencias, simplificando el despliegue en Lambda.

## Cómo Construir y Desplegar

1.  **Construir el JAR:**
    ```bash
    mvn clean package
    ```
    Esto generará el archivo `pokemon-type-lambda-1.0.0.jar` en el directorio `target/`.

2.  **Desplegar en AWS Lambda:**
    *   Cargar el JAR generado a una nueva función Lambda.
    *   Configurar el `Handler` como `com.pokemon.api.Handler::handleRequest`.
    *   Ajustar la memoria y el tiempo de espera.

3.  **Configurar API Gateway:**
    *   Crear una nueva REST API.
    *   Crear un recurso `/pokemons/type/{type}`.
    *   Crear un método `GET` para este recurso.
    *   Configurar la integración con la función Lambda.
    *   Desplegar la API.

## Ejemplo de Uso

Una vez desplegado, se puede invocar la API a través de la siguiente URL. Simplemente reemplaza el tipo de Pokémon al final de la URL (por ejemplo, `fire`, `water`, `grass`).

**URL Base:** `https://ryikgo3dob.execute-api.us-east-2.amazonaws.com/dev/pokemons/type/`

**Ejemplo con el tipo `water`:**
[https://ryikgo3dob.execute-api.us-east-2.amazonaws.com/dev/pokemons/type/water](https://ryikgo3dob.execute-api.us-east-2.amazonaws.com/dev/pokemons/type/water)

**Respuesta de Éxito (Ejemplo para `fire`):**

```json
[
  {
    "name": "charmander",
    "url": "https://pokeapi.co/api/v2/pokemon/4/"
  },
  {
    "name": "charmeleon",
    "url": "https://pokeapi.co/api/v2/pokemon/5/"
  }
]
```

**Respuesta de Error (Tipo no encontrado - 404):**

```json
{
  "error": "No se encontraron Pokémon para el tipo: invalidtype"
}
```

**Respuesta de Error (Parámetro faltante - 400):**

```json
{
  "error": "El tipo de Pokémon es un parámetro requerido."
}
```