# Microservicio 3: Comparador de Pokémon (AWS Lambda & Python)

Este microservicio es una función AWS Lambda desarrollada en Python que compara la "fortaleza" de dos Pokémon y almacena el resultado en un bucket de Amazon S3. La función se expone a través de Amazon API Gateway.

## Arquitectura y Diseño

### Strategy Pattern

Para determinar cuál Pokémon es más fuerte, se ha implementado el **Strategy Pattern**. Esto permite que el algoritmo de comparación sea intercambiable y extensible sin modificar la lógica principal de la función Lambda.

- **Interfaz de la Estrategia (`ComparisonStrategy` en `comparison_strategies.py`):** Define un método `compare` que todas las estrategias concretas deben implementar.
- **Estrategia Concreta (`SumOfBaseStatsStrategy` en `comparison_strategies.py`):** La implementación actual. Compara a los Pokémon basándose en la suma de sus estadísticas base: HP, Ataque, Defensa y Velocidad.
- **Contexto (Función Lambda en `lambda_function.py`):** La función Lambda utiliza la interfaz de la estrategia para realizar la comparación, delegando el cálculo a la estrategia concreta seleccionada.

Este patrón fue elegido para facilitar la adición de nuevos criterios de comparación en el futuro (por ejemplo, ventaja por tipo, efectividad de habilidades, etc.) con un impacto mínimo en el código existente.

### Estructura del Proyecto

```
.
├── lambda_function.py      # Punto de entrada y lógica principal de la Lambda.
├── pokemon_service.py      # Cliente para consumir el Microservicio 1.
├── comparison_strategies.py# Implementación del Strategy Pattern.
├── s3_handler.py           # Lógica para interactuar con S3.
├── utils.py                # Funciones de utilidad (formato de respuesta).
├── requirements.txt        # Dependencias de Python.
└── README.md               # Esta documentación.
```

## Despliegue Manual en AWS

Siga estos pasos para desplegar la función Lambda manualmente a través de la consola de AWS.

### 1. Preparar el Paquete de Despliegue

El código y sus dependencias deben ser empaquetados en un archivo `.zip`.

a. **Instalar dependencias en una carpeta local:**
   Cree una carpeta para el paquete y instale las dependencias allí.
   ```bash
   mkdir package
   pip install -r requirements.txt -t ./package
   ```

b. **Copiar el código de la aplicación:**
   Copie todos los archivos `.py` a la carpeta `package`.
   ```bash
   cp *.py ./package/
   ```

c. **Crear el archivo .zip:**
   Navegue dentro de la carpeta `package` y comprima su contenido.
   ```bash
   cd package
   zip -r ../deployment_package.zip .
   cd ..
   ```
   Ahora tendrá un archivo `deployment_package.zip` listo para ser subido.

### 2. Crear la Función Lambda

a. **Navegue a la consola de AWS Lambda** y haga clic en "Crear función".
b. Seleccione "Crear desde cero".
c. **Configuración básica:**
   - **Nombre de la función:** `compare-pokemons-function` (o el nombre que prefiera).
   - **Runtime:** `Python 3.12`.
   - **Arquitectura:** `x86_64`.
d. **Permisos:**
   - Expanda "Cambiar rol de ejecución predeterminado".
   - Seleccione "Crear un nuevo rol con permisos básicos de Lambda".
   - Dele un nombre descriptivo al rol, como `compare-pokemons-lambda-role`.
e. Haga clic en "Crear función".

### 3. Configurar el Rol de IAM

a. Vaya a la **consola de IAM**, busque el rol recién creado (`compare-pokemons-lambda-role`).
b. En la pestaña "Políticas de permisos", haga clic en "Agregar permisos" > "Crear política en línea".
c. Seleccione el servicio **S3**.
d. En "Acciones", busque y seleccione `PutObject`.
e. En "Recursos", seleccione "Específico" y haga clic en "Agregar ARN".
f. Ingrese el ARN de su bucket de S3, añadiendo `/*` al final para permitir la escritura de cualquier objeto. Ejemplo: `arn:aws:s3:::your-pokemon-comparison-bucket/*`.
g. Dele un nombre a la política (ej. `S3PutObjectPolicy`) y créela.

### 4. Subir el Código y Configurar la Lambda

a. De vuelta en la página de su función Lambda, en la pestaña "Código", haga clic en "Cargar desde" y seleccione ".zip file".
b. Suba el archivo `deployment_package.zip`.
c. **Configuración del Handler:**
   - Vaya a la pestaña "Configuración" > "Editar".
   - Cambie el "Handler" a `lambda_function.lambda_handler`.
d. **Variables de Entorno:**
   - En la pestaña "Configuración" > "Variables de entorno", agregue las siguientes variables:
     - `POKEMON_SERVICE_URL`: La URL base de su Microservicio 1.
     - `S3_BUCKET_NAME`: El nombre de su bucket de S3.

### 5. Añadir el Disparador de API Gateway

a. En la página de la función Lambda, vaya a la pestaña "Configuración" y seleccione "Disparadores".
b. Haga clic en "Agregar disparador".
c. Seleccione **API Gateway** en la lista.
d. Elija "Crear una nueva API".
e. Seleccione el tipo de API **HTTP API**.
f. En "Seguridad", elija "Abrir".
g. Haga clic en "Agregar".

### 6. Cómo Probar la API

Una vez configurado el disparador, la consola de Lambda le proporcionará una **URL de punto de enlace de la API** en la sección "Disparadores".

**Endpoint:** `POST`

**URL de Ejemplo:**
La URL tendrá una estructura similar a esta:
`https://q0r4nvlkyk.execute-api.us-east-2.amazonaws.com/default/ComparePokemonsFunction`

**Nota:** El identificador `q0r4nvlkyk` y la ruta `/default/ComparePokemonsFunction` pueden variar según su configuración específica. Use siempre la URL que le proporciona su consola de AWS.

**Ejemplo de solicitud con `curl`:**

```bash
curl --location --request POST 'https://q0r4nvlkyk.execute-api.us-east-2.amazonaws.com/default/ComparePokemonsFunction' \
--header 'Content-Type: application/json' \
--data-raw '{
    "pokemon1": "pikachu",
    "pokemon2": "mewtwo"
}'
```

### Respuestas

- **Éxito (200 OK):**
  ```json
  {
    "result": "Pikachu (220 stats) es más fuerte que Charmander (209 stats)."
  }
  ```

- **Pokémon no encontrado (404 Not Found):**
  ```json
  {
    "error": "El Pokémon 'nonexistent' no fue encontrado."
  }
  ```

- **Solicitud inválida (400 Bad Request):**
  ```json
  {
    "error": "Faltan los nombres de los Pokémon en la solicitud."
  }
