package com.lgtm.easymoney.exceptions.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.lgtm.easymoney.configs.DbConsts;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.exceptions.UnauthorizedException;
import com.lgtm.easymoney.payload.rsp.ErrorRsp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
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
  @Value("${app.jwt.header}")
  private String tokenRequestHeader;

  /** This handles exceptions caused by correct data types but invalid data ranges in body. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorRsp> handle(final MethodArgumentNotValidException ex) {
    List<String> errorFields = new ArrayList<>();
    var fieldErrors = ex.getBindingResult().getFieldErrors();
    for (var fieldError : fieldErrors) {
      errorFields.add(fieldError.getField());
    }
    String errorMessage = "Invalid input format!";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles exceptions caused by unauthorized client operations. */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorRsp> handle(final UnauthorizedException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorRsp(List.of(tokenRequestHeader), ex.getMessage()));
  }

  /** This handles exceptions caused by invalid path variables. */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorRsp> handle(final MethodArgumentTypeMismatchException ex) {
    List<String> errorFields = new ArrayList<>();
    errorFields.add(ex.getName());
    String errorMessage = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles exceptions caused by incorrect data types. */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorRsp> handle(
      final HttpMessageNotReadableException ex) throws IOException {
    var jsonEx = (JsonParseException) ex.getCause();  // other kinds of causes shouldn't happen
    List<String> errorFields = new ArrayList<>();
    errorFields.add(jsonEx.getProcessor().currentName());
    String errorMessage = "JSON parse error!";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles when a resource is not found in DB. */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorRsp> handle(final ResourceNotFoundException ex) {
    List<String> errorFields = new ArrayList<>();
    errorFields.add(ex.getFieldName());
    String errorMessage = ex.getMessage();
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles when a database constraint
   * (e.g., unique) is violated when creating/updating data. */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorRsp> handle(final DataIntegrityViolationException ex) {
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
  public ResponseEntity<ErrorRsp> handle(final InvalidUpdateException ex) {
    List<String> errorFields = new ArrayList<>();
    errorFields.add(ex.getFieldName());
    String errorMessage = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorRsp(errorFields, errorMessage));
  }

  /** This handles inapplicable client operations. */
  @ExceptionHandler(InapplicableOperationException.class)
  public ResponseEntity<ErrorRsp> handle(final InapplicableOperationException ex) {
    List<String> errorFields = new ArrayList<>();
    errorFields.add(ex.getFieldName());
    String errorMessage = ex.getMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorRsp(errorFields, errorMessage));
  }
}