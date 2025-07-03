import json

def create_api_gateway_response(status_code, body):
    """
    Crea una respuesta estándar para API Gateway.

    :param status_code: Código de estado HTTP.
    :param body: Cuerpo de la respuesta (diccionario).
    :return: Diccionario de respuesta formateado para API Gateway.
    """
    return {
        'statusCode': status_code,
        'headers': {
            'Content-Type': 'application/json'
        },
        'body': json.dumps(body)
    }
