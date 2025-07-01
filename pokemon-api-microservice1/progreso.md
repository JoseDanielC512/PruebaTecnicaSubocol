# Progreso del Desarrollo del Microservicio Pokémon

Este documento sigue el progreso del desarrollo basado en `instrucciones.md`.

## Plan de Desarrollo

### 1. Configuración Inicial del Proyecto
- [x] Verificar que el proyecto existente cumple con la configuración de Spring Initializr.
- [x] Validar dependencias: `spring-boot-starter-web`, `spring-boot-starter-test`.
- [x] Confirmar versión de Java 17.
- [x] Revisar `pom.xml` para `groupId`, `artifactId`.

### 2. Estructura de Paquetes y Clases
- [ ] Crear paquete `com.example.pokemonapi.controller`.
- [ ] Crear paquete `com.example.pokemonapi.service`.
- [ ] Crear paquete `com.example.pokemonapi.model`.
- [ ] Crear paquete `com.example.pokemonapi.exception`.
- [ ] Crear clase `PokemonController` en el paquete `controller`.
- [ ] Crear clase `PokemonService` en el paquete `service`.
- [ ] Crear clase `PokemonStatsResponse` con Builder interno en el paquete `model`.
- [ ] Crear POJOs para la respuesta de PokeAPI en el paquete `model`.
- [ ] Crear clase `PokemonNotFoundException` en el paquete `exception`.

### 3. Consumo de la PokeAPI
- [ ] Crear clase de configuración `AppConfig`.
- [ ] Definir un bean de `RestTemplate` en `AppConfig`.
- [ ] Implementar la lógica en `PokemonService` para llamar a la PokeAPI usando `RestTemplate`.

### 4. Implementación del Patrón Builder
- [ ] Implementar la clase `PokemonStatsResponse` con su `Builder` estático interno según el ejemplo.
- [ ] Usar el `Builder` en `PokemonService` para transformar la respuesta de la PokeAPI.

### 5. Pruebas Unitarias
- [ ] Escribir pruebas unitarias para `PokemonController`.
- [ ] Escribir pruebas unitarias para `PokemonService` (mockeando `RestTemplate`).
- [ ] Probar caso de éxito (Pokémon encontrado).
- [ ] Probar caso de error (Pokémon no encontrado).

### 6. Manejo de Errores
- [ ] Crear un `@ControllerAdvice` para manejar excepciones globalmente.
- [ ] Implementar un `@ExceptionHandler` para `PokemonNotFoundException` que devuelva 404.
- [ ] Implementar un `@ExceptionHandler` para excepciones generales que devuelva 500.

### 7. Compilación y Ejecución
- [ ] Compilar el proyecto con `mvn clean install`.
- [ ] Ejecutar la aplicación con `mvn spring-boot:run`.
- [ ] Probar el endpoint con `curl http://localhost:8080/pokemon/pikachu`.

### 8. Notas Finales
- [ ] Añadir Javadoc a las clases principales, especialmente al Builder.
- [ ] Actualizar el `README.md` si es necesario.
