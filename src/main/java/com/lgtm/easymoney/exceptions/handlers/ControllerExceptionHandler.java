package com.lgtm.easymoney.exceptions.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.lgtm.easymoney.payload.ErrorRsp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class ControllerExceptionHandler {
    /** This handles exceptions caused by correct data types but invalid data ranges. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRsp> handle(MethodArgumentNotValidException ex) {
        List<String> errorFields = new ArrayList<>();
        String errorMessage;
        try {
            var fieldErrors = Objects.requireNonNull(ex.getBindingResult().getFieldErrors());
            for (var fieldError : fieldErrors) {
                errorFields.add(fieldError.getField());
            }
            errorMessage = "Invalid input format!";
        } catch (NullPointerException npe) {
            // TODO not sure when `fieldErrors` is null
            errorMessage = "Unknown input error!";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorRsp(errorFields, errorMessage));
    }

    /** This handles exceptions caused by incorrect data types. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRsp> handle(HttpMessageNotReadableException ex) throws IOException {
        List<String> errorFields = new ArrayList<>();
        String errorMessage;
        var cause = ex.getCause();

        if (cause instanceof JsonParseException) {
            /* If there are multiple problematic fields, this kind of exception only includes the first one. */
            JsonParseException jsonEx = (JsonParseException) ex.getCause();
            errorFields.add(jsonEx.getProcessor().currentName());
            errorMessage = "JSON parse error!";
        } else {
            // TODO not sure about other causes
            errorMessage = "Unknown http read error!";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorRsp(errorFields, errorMessage));
    }
}