# Microservicio 3: Comparador de Pokémon (AWS Lambda & Python)

Este microservicio es una función AWS Lambda desarrollada en Python que cumple dos funciones principales:
1.  Compara la "fortaleza" de dos Pokémon basándose en la suma de sus estadísticas base.
2.  Almacena el resultado de dicha comparación en un archivo de texto (`.txt`) dentro de un bucket de Amazon S3.

La funcionalidad se expone a través de un endpoint de Amazon API Gateway.

## Arquitectura y Diseño

### Strategy Pattern

Para determinar cuál Pokémon es más fuerte, se ha implementado el **Strategy Pattern**. Esto permite que el algoritmo de comparación sea intercambiable y extensible sin modificar la lógica principal de la función Lambda.

-   **Interfaz de la Estrategia (`ComparisonStrategy` en `comparison_strategies.py`):** Define un método `compare` que todas las estrategias concretas deben implementar.
-   **Estrategia Concreta (`SumOfBaseStatsStrategy` en `comparison_strategies.py`):** La implementación actual. Compara a los Pokémon basándose en la suma de sus estadísticas base: HP, Ataque, Defensa y Velocidad.
-   **Contexto (Función Lambda en `lambda_function.py`):** La función Lambda utiliza la interfaz de la estrategia para realizar la comparación, delegando el cálculo a la estrategia concreta seleccionada.

Este patrón fue elegido para facilitar la adición de nuevos criterios de comparación en el futuro (por ejemplo, ventaja por tipo, efectividad de habilidades, etc.) con un impacto mínimo en el código existente.

### Interacción con el Microservicio 1 (Java Spring Boot en Elastic Beanstalk)

Para obtener las estadísticas de los Pokémon, este microservicio actúa como cliente del **Microservicio 1**, el cual está desplegado en un entorno de **AWS Elastic Beanstalk**.

* **`pokemon_service.py`:** Este módulo actúa como el cliente HTTP para interactuar con el Microservicio 1. Se encarga de construir la URL completa y realizar la solicitud GET para obtener los datos de un Pokémon específico.
* **Consumo de la URL:** La dirección del Microservicio 1 **no está codificada directamente** en el código. En su lugar, se obtiene en tiempo de ejecución a través de la variable de entorno de Lambda `POKEMON_SERVICE_URL`. Esta variable contendrá la URL base de tu Microservicio 1 en Elastic Beanstalk (ej., `http://pokemonapimicroservice1-env.eba-cvsrjd4h.us-east-2.elasticbeanstalk.com/pokemon/`).
* **Formato de Datos:** Se espera que el Microservicio 1 retorne un objeto JSON con las estadísticas del Pokémon en el formato previamente definido, el cual incluye `hp`, `ataque`, `defensa` y `velocidad`, cruciales para el cálculo de fortaleza.

### Estructura del Proyecto

```

.
├── lambda\_function.py      \# Punto de entrada y lógica principal de la Lambda.
├── pokemon\_service.py      \# Cliente para consumir el Microservicio 1.
├── comparison\_strategies.py\# Implementación del Strategy Pattern.
├── s3\_handler.py           \# Lógica para interactuar con S3.
├── utils.py                \# Funciones de utilidad (formato de respuesta).
├── requirements.txt        \# Dependencias de Python.
└── README.md               \# Esta documentación.

````

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
     - `POKEMON_SERVICE_URL`: La URL base de su Microservicio 1. Esta es la dirección de su API REST alojada en Elastic Beanstalk (ej., `https://tu-app.elasticbeanstalk.com/pokemon/`).
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
curl --location --request POST '[https://q0r4nvlkyk.execute-api.us-east-2.amazonaws.com/default/ComparePokemonsFunction](https://q0r4nvlkyk.execute-api.us-east-2.amazonaws.com/default/ComparePokemonsFunction)' \
--header 'Content-Type: application/json' \
--data-raw '{
    "pokemon1": "pikachu",
    "pokemon2": "mewtwo"
}'
````

### Respuestas

  - **Éxito (200 OK):**
      ` json   {     "result": "Mewtwo (414 stats) es más fuerte que Pikachu (220 stats)."   }    `

  - **Pokémon no encontrado (404 Not Found):**
      ` json   {     "error": "El Pokémon 'nonexistent' no fue encontrado."   }    `

  - **Solicitud inválida (400 Bad Request):**
      ` json   {     "error": "Faltan los nombres de los Pokémon en la solicitud."   }    `

### Salida en S3

Además de la respuesta de la API, cada comparación exitosa genera un archivo `.txt` en el bucket de S3 configurado.

**Formato del Nombre del Archivo:**
El nombre del archivo es único para cada solicitud y sigue el formato:
`comparison_{pokemon1}_vs_{pokemon2}_{aws_request_id}.txt`

**Ejemplo de Contenido del Archivo:**
El contenido del archivo es una cadena de texto simple con el resultado de la comparación.

```
Charizard (340 stats) es más fuerte que Pikachu (220 stats).
```