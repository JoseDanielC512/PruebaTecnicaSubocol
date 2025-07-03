import json
import os
import logging
from requests.exceptions import RequestException

from utils import create_api_gateway_response
from pokemon_service import fetch_pokemon_data, PokemonNotFoundError
from s3_handler import save_result_to_s3
from comparison_strategies import SumOfBaseStatsStrategy

# Configuración del logger
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Obtener variables de entorno
POKEMON_SERVICE_URL = os.environ.get('POKEMON_SERVICE_URL')
S3_BUCKET_NAME = os.environ.get('S3_BUCKET_NAME')

def lambda_handler(event, context):
    """
    Punto de entrada principal para la función AWS Lambda.
    """
    # Validar configuración esencial
    if not POKEMON_SERVICE_URL or not S3_BUCKET_NAME:
        logger.error("Variables de entorno POKEMON_SERVICE_URL o S3_BUCKET_NAME no están configuradas.")
        return create_api_gateway_response(500, {'error': 'Configuración del servidor incompleta.'})

    # Validar y parsear el cuerpo de la solicitud
    try:
        body = json.loads(event.get('body', '{}'))
        pokemon1_name = body.get('pokemon1')
        pokemon2_name = body.get('pokemon2')
        if not pokemon1_name or not pokemon2_name:
            return create_api_gateway_response(400, {'error': 'Faltan los nombres de los Pokémon en la solicitud.'})
    except json.JSONDecodeError:
        return create_api_gateway_response(400, {'error': 'Cuerpo de la solicitud mal formado.'})

    try:
        # Obtener datos de los Pokémon
        pokemon1_stats = fetch_pokemon_data(pokemon1_name.lower(), POKEMON_SERVICE_URL)
        pokemon2_stats = fetch_pokemon_data(pokemon2_name.lower(), POKEMON_SERVICE_URL)

        # Utilizar el Strategy Pattern para la comparación
        strategy = SumOfBaseStatsStrategy()
        result_string = strategy.compare(pokemon1_stats, pokemon2_stats)

        # Guardar el resultado en S3
        file_name = f"comparison_{pokemon1_name}_vs_{pokemon2_name}_{context.aws_request_id}.txt"
        if not save_result_to_s3(S3_BUCKET_NAME, file_name, result_string):
             # Si falla el guardado en S3, se considera un error interno pero se devuelve el resultado al cliente.
            logger.error(f"No se pudo guardar el resultado en S3, pero la comparación fue exitosa.")
            # No se retorna error 500 para que el usuario reciba la respuesta de la comparación.
            # En un escenario real, esto podría encolar una tarea para reintentar el guardado.

        # Devolver respuesta exitosa
        return create_api_gateway_response(200, {'result': result_string})

    except PokemonNotFoundError as e:
        logger.warning(str(e))
        return create_api_gateway_response(404, {'error': str(e)})
    except RequestException as e:
        logger.error(f"Error de comunicación con el servicio de Pokémon: {e}")
        return create_api_gateway_response(502, {'error': 'No se pudo comunicar con el servicio de Pokémon.'})
    except Exception as e:
        logger.error(f"Error inesperado: {e}", exc_info=True)
        return create_api_gateway_response(500, {'error': 'Ocurrió un error interno inesperado.'})
