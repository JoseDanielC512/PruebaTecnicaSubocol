package com.pokemon.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase de utilidad para construir respuestas estandarizadas para API Gateway.
 * Facilita la creación de respuestas JSON con códigos de estado HTTP apropiados.
 */
public class ApiResponse {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Crea una respuesta de éxito con un cuerpo JSON y código de estado 200.
     *
     * @param body El objeto a serializar como cuerpo JSON.
     * @return APIGatewayProxyResponseEvent con la respuesta de éxito.
     */
    public static APIGatewayProxyResponseEvent success(Object body) {
        return buildResponse(200, GSON.toJson(body));
    }

    /**
     * Crea una respuesta de error con un cuerpo JSON y un código de estado específico.
     *
     * @param statusCode El código de estado HTTP del error (ej., 400, 404, 500).
     * @param message El mensaje de error.
     * @return APIGatewayProxyResponseEvent con la respuesta de error.
     */
    public static APIGatewayProxyResponseEvent error(int statusCode, String message) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", message);
        return buildResponse(statusCode, GSON.toJson(errorBody));
    }

    /**
     * Construye un APIGatewayProxyResponseEvent con el código de estado y el cuerpo dados.
     *
     * @param statusCode El código de estado HTTP.
     * @param body El cuerpo de la respuesta (ya en formato JSON string).
     * @return APIGatewayProxyResponseEvent.
     */
    private static APIGatewayProxyResponseEvent buildResponse(int statusCode, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(statusCode);
        response.setBody(body);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*"); // CORS para permitir llamadas desde cualquier origen
        response.setHeaders(headers);
        return response;
    }
}
