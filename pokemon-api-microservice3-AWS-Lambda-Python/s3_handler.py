import boto3
import logging
from botocore.exceptions import ClientError

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def save_result_to_s3(bucket_name, file_name, content):
    """
    Guarda un archivo de texto en un bucket de S3.

    :param bucket_name: Nombre del bucket de S3.
    :param file_name: Nombre del archivo a crear en S3.
    :param content: Contenido del archivo.
    :return: True si fue exitoso, False en caso contrario.
    """
    s3_client = boto3.client('s3')
    try:
        s3_client.put_object(Bucket=bucket_name, Key=file_name, Body=content)
        logger.info(f"Resultado guardado exitosamente en s3://{bucket_name}/{file_name}")
        return True
    except ClientError as e:
        logger.error(f"Error al guardar en S3: {e}")
        return False
