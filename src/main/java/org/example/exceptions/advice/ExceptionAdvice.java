package org.example.exceptions.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.exceptions.MarketPlaceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class ExceptionAdvice {
    private static final String EXCEPTION = "Exception {}";
    private static final String END_USER_MESSAGE = "endUserMessage";
    private static final String INTERNAL_MESSAGE = "internalMessage";

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handle(MarketPlaceException ex) {
        log.error(EXCEPTION, ex.getInternalMessage());
        Map<String, Object> map = new HashMap<>();
        map.put(END_USER_MESSAGE, ex.getEndUserMessage());
        map.put(INTERNAL_MESSAGE, ex.getInternalMessage());
        return ResponseEntity.status(ex.getStatus()).body(map);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handle(HttpClientErrorException ex) {
        log.error(EXCEPTION, ex.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put(END_USER_MESSAGE, ex.getMessage());
        map.put(INTERNAL_MESSAGE, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(map);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handle(HttpServerErrorException ex) {
        log.error(EXCEPTION, ex.getMessage());
        Map<String, Object> map = new HashMap<>();
        map.put(END_USER_MESSAGE, "Something went wrong, please try after sometime!");
        map.put(INTERNAL_MESSAGE, ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(map);
    }
}
