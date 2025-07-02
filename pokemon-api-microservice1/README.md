# Microservicio de API de Pokémon

Este proyecto es un microservicio RESTful desarrollado en Java con Spring Boot. Su función principal es consumir la [PokeAPI](https://pokeapi.co/) pública para obtener datos de un Pokémon por su nombre y devolver una respuesta JSON simplificada y en español con sus estadísticas más relevantes.

## Características Principales

- **Framework**: Spring Boot 3.
- **Lenguaje**: Java 17.
- **Dependencias**: `spring-boot-starter-web`, `spring-boot-starter-test`.
- **Arquitectura**: API REST con una estructura de 3 capas (Controlador, Servicio, Modelo).
- **Patrón de Diseño**: Se utiliza el **Patrón Builder** para construir el objeto de respuesta (`PokemonStatsResponse`) de forma segura e inmutable.

## Estructura del Proyecto

El código fuente está organizado en los siguientes paquetes principales:

- `com.example.pokemonapi.config`: Clases de configuración de Spring (e.g., `AppConfig` para el bean `RestTemplate`).
- `com.example.pokemonapi.controller`: Controladores REST que manejan las solicitudes HTTP (e.g., `PokemonController`).
- `com.example.pokemonapi.exception`: Clases para el manejo de excepciones, incluyendo un manejador global (`GlobalExceptionHandler`).
- `com.example.pokemonapi.model`: Clases POJO que representan los datos, tanto los mapeados desde la PokeAPI (`Pokemon`) como el DTO de respuesta (`PokemonStatsResponse`).
- `com.example.pokemonapi.service`: Clases de servicio que contienen la lógica de negocio principal (`PokemonService`).

## Cómo Empezar

A continuación se detallan los pasos para compilar, probar y ejecutar la aplicación.

### Prerrequisitos

- Tener instalado Java 17 (o superior).
- Tener instalado [Apache Maven](https://maven.apache.org/download.cgi).

### 1. Ejecutar las Pruebas Unitarias

Para asegurar que todos los componentes funcionan como se espera, puedes ejecutar el conjunto de pruebas unitarias y de integración con el siguiente comando desde la raíz del proyecto:

```bash
mvn test
```

Esto compilará el código y correrá todas las pruebas ubicadas en `src/test/java`.

### 2. Ejecutar la Aplicación

Para iniciar el microservicio, ejecuta el siguiente comando de Maven. La aplicación se iniciará y estará disponible en el puerto 8080 por defecto.

```bash
mvn spring-boot:run
```

## Consumir la API

Una vez que la aplicación está en ejecución, puedes consumir el endpoint de las siguientes maneras:

### Mediante el Navegador

Simplemente haz clic en el siguiente enlace o cópialo y pégalo en tu navegador:

[http://localhost:8080/pokemon/pikachu](http://localhost:8080/pokemon/pikachu)

Puedes cambiar `pikachu` por el nombre de cualquier otro Pokémon (e.g., `ditto`, `charizard`, `mewtwo`).

### Mediante la Consola (curl)

Abre una terminal y utiliza `curl` para hacer una solicitud:

```bash
curl http://localhost:8080/pokemon/pikachu
```

### Ejemplo de Respuesta Exitosa

Para una solicitud a `/pokemon/pikachu`, la API devolverá el siguiente objeto JSON con las claves en español:

```json
{
  "nombre": "pikachu",
  "hp": 35,
  "ataque": 55,
  "defensa": 40,
  "velocidad": 90,
  "tipos": [
    "electric"
  ],
  "habilidades": [
    "static",
    "lightning-rod"
  ]
}
```

### Ejemplo de Pokémon no Encontrado

Si solicitas un Pokémon que no existe (e.g., `/pokemon/aguacate`), la API devolverá una respuesta `404 Not Found` con el siguiente formato:

```json
{
  "fecha": "02/07/2025 18:30:00",
  "estado": 404,
  "error": "Pokemon no encontrado",
  "mensaje": "Pokémon 'aguacate' no encontrado.",
  "ruta": "/pokemon/aguacate"
}
``` 