package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.rsp.AnalyticRsp;
import com.lgtm.easymoney.services.AnalyticService;
import com.lgtm.easymoney.services.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Analytic controller.
 */
@RestController
@RequestMapping("/analytic")
public class AnalyticController {
  private final AnalyticService analyticService;

  @Autowired
  public AnalyticController(AnalyticService analyticService) {
    this.analyticService = analyticService;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Method to retrieve the analytic report of a user by user ID.")
  public ResponseEntity<AnalyticRsp> getAnalytic(@PathVariable(value = "id") @NotNull Long id) {
    return new ResponseEntity<>(analyticService.getAnalytic(id), HttpStatus.OK);
  }
}
