# Instrucciones para el Desarrollo del Microservicio 1

Este documento detalla cómo desarrollar el Microservicio 1, una API REST en Java con Spring Boot que consume la [PokeAPI](https://pokeapi.co/) para retornar un JSON simplificado con las estadísticas principales de un Pokémon (nombre, HP, ataque, defensa, velocidad, tipos y habilidades) dado su nombre. Para transformar la respuesta compleja de la PokeAPI en este formato simplificado, se implementa el **Patrón Builder** (Patrón Constructor).

## 1. Configuración Inicial del Proyecto
- **Herramienta:** Usa [Spring Initializr](https://start.spring.io/) para generar el proyecto.
- **Dependencias:** 
  - `spring-boot-starter-web` (para la API REST).
  - `spring-boot-starter-test` (para pruebas unitarias).
- **Versión de Java:** 17.
- **Detalles del Proyecto:**
  - Group: `com.example`
  - Artifact: `pokemon-api`
  - Package: `com.example.pokemonapi`
- Importa el proyecto en tu IDE (e.g., IntelliJ IDEA).

## 2. Estructura de Paquetes y Clases
Organiza el código en paquetes funcionales:
- `com.example.pokemonapi.controller`: Contiene `PokemonController`.
- `com.example.pokemonapi.service`: Contiene `PokemonService`.
- `com.example.pokemonapi.model`: Contiene modelos como `PokemonStatsResponse` y POJOs para la respuesta de la PokeAPI.
- `com.example.pokemonapi.exception`: Contiene excepciones personalizadas como `PokemonNotFoundException`.

### Clases Principales:
- **`PokemonController`:** Maneja solicitudes HTTP (e.g., `GET /pokemon/{name}`).
- **`PokemonService`:** Lógica de negocio, consume la PokeAPI y usa el Builder para construir la respuesta.
- **`PokemonStatsResponse`:** Modelo de respuesta simplificado con un Builder interno.

## 3. Consumo de la PokeAPI
- **Cliente HTTP:** Usa `RestTemplate` para realizar solicitudes a `https://pokeapi.co/api/v2/pokemon/{name}`.
- **Configuración:** Define un bean de `RestTemplate` en una clase `@Configuration` (e.g., `AppConfig`).
- **Mapeo:** Crea POJOs para deserializar la respuesta de la PokeAPI (e.g., `PokeApiPokemonResponse`, `Stat`, `Type`, `Ability`).

## 4. Implementación del Patrón Builder
### Por qué el Patrón Builder es el más Adecuado
El **Patrón Builder** se utiliza para construir el objeto `PokemonStatsResponse` por las siguientes razones:
- **Construcción Paso a Paso:** La respuesta de la PokeAPI es compleja, con estructuras anidadas (e.g., listas de `stats`, `types`, `abilities`). El Builder permite extraer y mapear estos datos incrementalmente en un objeto simplificado.
- **Manejo de Campos Anidados y Listas:** Facilita la extracción de datos de listas y sub-objetos (e.g., nombres de tipos y habilidades) sin ensuciar la lógica del servicio.
- **Separación de Lógica:** Encapsula la construcción del objeto de salida, separándola de la lógica de negocio en `PokemonService`.
- **Inmutabilidad:** Garantiza que el objeto `PokemonStatsResponse` sea inmutable tras su construcción, una buena práctica para DTOs en APIs.

### Cómo Aplicar el Patrón Builder
El modelo `PokemonStatsResponse` incluye una clase estática interna `Builder` para construir la respuesta simplificada. Ejemplo de implementación:

```java
public class PokemonStatsResponse {
    private final String name;
    private final int hp;
    private final int attack;
    private final int defense;
    private final int speed;
    private final List<String> types;
    private final List<String> abilities;

    // Constructor privado para uso exclusivo del Builder
    private PokemonStatsResponse(Builder builder) {
        this.name = builder.name;
        this.hp = builder.hp;
        this.attack = builder.attack;
        this.defense = builder.defense;
        this.speed = builder.speed;
        this.types = builder.types;
        this.abilities = builder.abilities;
    }

    // Getters públicos
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public List<String> getTypes() { return types; }
    public List<String> getAbilities() { return abilities; }

    // Clase estática Builder
    public static class Builder {
        private String name;
        private int hp;
        private int attack;
        private int defense;
        private int speed;
        private List<String> types = new ArrayList<>();
        private List<String> abilities = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder hp(int hp) {
            this.hp = hp;
            return this;
        }

        public Builder attack(int attack) {
            this.attack = attack;
            return this;
        }

        public Builder defense(int defense) {
            this.defense = defense;
            return this;
        }

        public Builder speed(int speed) {
            this.speed = speed;
            return this;
        }

        public Builder addType(String type) {
            this.types.add(type);
            return this;
        }

        public Builder addAbility(String ability) {
            this.abilities.add(ability);
            return this;
        }

        public PokemonStatsResponse build() {
            return new PokemonStatsResponse(this);
        }
    }
}
```

### Uso en `PokemonService`
En el servicio, el Builder se utiliza para transformar la respuesta de la PokeAPI en el objeto simplificado:

```java
public PokemonStatsResponse getPokemonStats(String pokemonName) {
    PokeApiPokemonResponse pokeApiResponse = callPokeApi(pokemonName);

    PokemonStatsResponse.Builder builder = new PokemonStatsResponse.Builder()
        .name(pokeApiResponse.getName());

    // Extraer estadísticas
    for (PokeApiPokemonResponse.StatEntry statEntry : pokeApiResponse.getStats()) {
        switch (statEntry.getStat().getName()) {
            case "hp": builder.hp(statEntry.getBase_stat()); break;
            case "attack": builder.attack(statEntry.getBase_stat()); break;
            case "defense": builder.defense(statEntry.getBase_stat()); break;
            case "speed": builder.speed(statEntry.getBase_stat()); break;
        }
    }

    // Extraer tipos
    for (PokeApiPokemonResponse.TypeEntry typeEntry : pokeApiResponse.getTypes()) {
        builder.addType(typeEntry.getType().getName());
    }

    // Extraer habilidades
    for (PokeApiPokemonResponse.AbilityEntry abilityEntry : pokeApiResponse.getAbilities()) {
        builder.addAbility(abilityEntry.getAbility().getName());
    }

    return builder.build();
}
```

## 5. Pruebas Unitarias
- **Frameworks:** JUnit 5 y Mockito.
- **Clases Probadas:**
  - `PokemonController`: Verifica códigos HTTP (200 OK, 404 Not Found).
  - `PokemonService`: Simula `RestTemplate` y valida la construcción con el Builder.
- **Casos:** Flujo exitoso, errores (e.g., Pokémon no encontrado).

## 6. Manejo de Errores
- **Excepción:** `PokemonNotFoundException` para Pokémon inexistentes.
- **Gestión:** Usa `@ControllerAdvice` con `@ExceptionHandler`.
- **Respuestas HTTP:**
  - 404: Pokémon no encontrado.
  - 500: Errores inesperados.

## 7. Compilación y Ejecución
- **Compilar:** `mvn clean install`.
- **Ejecutar:** `mvn spring-boot:run`.
- **Probar:** `curl http://localhost:8080/pokemon/pikachu`.

### Ejemplo de Respuesta
```json
{
  "name": "pikachu",
  "hp": 35,
  "attack": 55,
  "defense": 40,
  "speed": 90,
  "types": ["electric"],
  "abilities": ["static", "lightning-rod"]
}
```

## 8. Notas Finales
- **Documentación:** Agrega un `README.md` con estas instrucciones.
- **Código:** Usa Javadoc para documentar el uso del Builder.
- **Dependencias:** Verifica el `pom.xml`.

Estas instrucciones garantizan que el Microservicio 1 sea reproducible y explique claramente la implementación del Patrón Builder.