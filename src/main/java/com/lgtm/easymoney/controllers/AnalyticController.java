package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.rsp.AnalyticRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.AnalyticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Analytic controller.
 */
@RestController
@RequestMapping("/analytic")
@SecurityRequirement(name = "Authorization")
public class AnalyticController {
  private final AnalyticService analyticService;

  @Autowired
  public AnalyticController(AnalyticService analyticService) {
    this.analyticService = analyticService;
  }

  @GetMapping
  @Operation(summary = "Method to retrieve the analytic report of a user by user ID.")
  public ResponseEntity<AnalyticRsp> getAnalytic(
          @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    return new ResponseEntity<>(analyticService.getAnalytic(principal.get()), HttpStatus.OK);
  }
}
