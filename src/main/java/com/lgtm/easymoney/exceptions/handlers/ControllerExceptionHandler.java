package com.lgtm.easymoney.exceptions.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.lgtm.easymoney.configs.DbConsts;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.payload.ErrorRsp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


/**
 * Generic controller exception handler.
 */
@ControllerAdvice
public class ControllerExceptionHandler {
  /** This handles exceptions caused by correct data types but invalid data ranges in body. */
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

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles exceptions caused by invalid path variables. */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorRsp> handle(MethodArgumentTypeMismatchException ex) {
    List<String> errorFields = new ArrayList<>();
    errorFields.add(ex.getName());
    String errorMessage = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles exceptions caused by incorrect data types. */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorRsp> handle(HttpMessageNotReadableException ex) throws IOException {
    List<String> errorFields = new ArrayList<>();
    String errorMessage;
    var cause = ex.getCause();

    if (cause instanceof JsonParseException) {
      /* If there are multiple problematic fields,
      this kind of exception only includes the first one. */
      JsonParseException jsonEx = (JsonParseException) ex.getCause();
      errorFields.add(jsonEx.getProcessor().currentName());
      errorMessage = "JSON parse error!";
    } else {
      // TODO not sure about other causes
      errorMessage = "Unknown http read error!";
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles when a resource is not found in DB. */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorRsp> handle(ResourceNotFoundException ex) {
    List<String> errorFields = new ArrayList<>();
    errorFields.add(ex.getFieldName());
    String errorMessage = ex.getMessage();
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles when a database constraint
   * (e.g., unique) is violated when creating/updating data. */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorRsp> handle(DataIntegrityViolationException ex) {
    var cause = (ConstraintViolationException) ex.getCause();
    String constraint = cause.getConstraintName().split("\\.")[1];
    List<String> errorFields = Arrays.asList(DbConsts.CONSTRAINTS_FIELDS.get(constraint));
    String errorMessage = DbConsts.CONSTRAINTS_ERR_MSGS.get(constraint);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles when an update to a resource is invalid 
   * according to business logics. */
  @ExceptionHandler(InvalidUpdateException.class)
  public ResponseEntity<ErrorRsp> handle(InvalidUpdateException ex) {
    List<String> errorFields = new ArrayList<>();
    errorFields.add(ex.getFieldName());
    String errorMessage = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorRsp(errorFields, errorMessage));
  }
}