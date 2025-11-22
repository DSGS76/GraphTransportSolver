package com.io.graphtransportsolver.presentation.dto;

import com.io.graphtransportsolver.utils.Constants;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * DTO genérico para estructurar las respuestas de la API REST.
 * Proporciona una estructura consistente para todas las respuestas del sistema,
 * incluyendo datos, mensajes, estado de éxito y códigos de estado HTTP.
 *
 * @param <T> Tipo de datos que contendrá la respuesta
 * @author Duvan Gil
 * @version 1.0
 */
@Data
public class ApiResponseDTO<T> {

    /**
     * Datos contenidos en la respuesta de la API.
     * Puede ser de cualquier tipo dependiendo del contexto de la operación.
     */
    private T data;

    /**
     * Mensaje descriptivo del resultado de la operación.
     * Proporciona información adicional sobre el éxito o fallo de la operación.
     */
    private String message;

    /**
     * Indica si la operación fue exitosa o no.
     * true para operaciones exitosas, false para operaciones fallidas.
     */
    private boolean success;

    /**
     * Código de estado HTTP de la respuesta.
     * Refleja el estado de la operación (200, 400, 500, etc.).
     */
    private int status;

    /**
     * Marca de tiempo de cuando se generó la respuesta.
     * Permite rastrear cuándo se procesó la operación.
     */
    private LocalDateTime timestamp;

    /**
     * Configura la respuesta para una operación exitosa con datos.
     *
     * @param data Los datos a incluir en la respuesta
     */
    public void SuccessOperation(T data){
        setData(data);
        setMessage(Constants.Message.SUCCESS_OPERATION);
        setSuccess(true);
        setStatus(HttpStatus.OK.value());
        setTimestamp(LocalDateTime.now());
    }

    /**
     * Configura la respuesta para una operación exitosa sin datos.
     */
    public void SuccessOperation(){
        setData(null);
        setMessage(Constants.Message.SUCCESS_OPERATION);
        setSuccess(true);
        setStatus(HttpStatus.OK.value());
        setTimestamp(LocalDateTime.now());
    }

    /**
     * Configura la respuesta para una operación fallida sin datos.
     * Establece un error interno del servidor (500).
     */
    public void FailedOperation(){
        setData(null);
        setMessage(Constants.Message.ERROR_OPERATION);
        setSuccess(false);
        setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        setTimestamp(LocalDateTime.now());
    }

    /**
     * Configura la respuesta para una operación fallida con datos de error.
     * Establece un error interno del servidor (500).
     *
     * @param data Los datos de error a incluir en la respuesta
     */
    public void FailedOperation(T data){
        setData(data);
        setMessage(Constants.Message.ERROR_OPERATION);
        setSuccess(false);
        setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        setTimestamp(LocalDateTime.now());
    }

    /**
     * Configura la respuesta para una operación con solicitud incorrecta sin datos.
     * Establece un error de solicitud incorrecta (400).
     */
    public void BadOperation(){
        setData(null);
        setMessage(Constants.Message.BAD_OPERATION);
        setSuccess(false);
        setStatus(HttpStatus.BAD_REQUEST.value());
        setTimestamp(LocalDateTime.now());
    }

    /**
     * Configura la respuesta para una operación con solicitud incorrecta con datos.
     * Establece un error de solicitud incorrecta (400).
     *
     * @param data Los datos relacionados con la solicitud incorrecta
     */
    public void BadOperation(T data){
        setData(data);
        setMessage(Constants.Message.BAD_OPERATION);
        setSuccess(false);
        setStatus(HttpStatus.BAD_REQUEST.value());
        setTimestamp(LocalDateTime.now());
    }

}