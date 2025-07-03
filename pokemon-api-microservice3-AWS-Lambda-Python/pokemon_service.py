import requests
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

class PokemonNotFoundError(Exception):
    """Excepción para cuando un Pokémon no es encontrado."""
    pass

def fetch_pokemon_data(pokemon_name, base_url):
    """
    Obtiene los datos de un Pokémon desde el Microservicio 1.

    :param pokemon_name: Nombre del Pokémon a buscar.
    :param base_url: URL base del servicio de Pokémon.
    :return: Diccionario con las estadísticas del Pokémon.
    :raises PokemonNotFoundError: Si el Pokémon no se encuentra (404).
    :raises requests.exceptions.RequestException: Para otros errores de red.
    """
    url = f"{base_url}/pokemon/{pokemon_name}"
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()  # Lanza una excepción para códigos de error HTTP
        return response.json()
    except requests.exceptions.HTTPError as err:
        if err.response.status_code == 404:
            logger.error(f"Pokémon no encontrado: {pokemon_name}")
            raise PokemonNotFoundError(f"El Pokémon '{pokemon_name}' no fue encontrado.")
        else:
            logger.error(f"Error HTTP al obtener datos de {pokemon_name}: {err}")
            raise
    except requests.exceptions.RequestException as err:
        logger.error(f"Error de conexión al obtener datos de {pokemon_name}: {err}")
        raise
