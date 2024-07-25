package org.example.exceptions;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Getter
@AllArgsConstructor
public class MarketPlaceException extends RuntimeException {
    private final HttpStatus status;
    private final String endUserMessage;
    private final String internalMessage;
}
