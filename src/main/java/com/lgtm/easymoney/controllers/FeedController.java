package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.rsp.FeedRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.FeedService;
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
 *  controller for feed activities.
 */
@RestController
@RequestMapping("/feed")
@SecurityRequirement(name = "Authorization")
public class FeedController {
  final FeedService feedService;

  // initialize controller with feed service
  @Autowired
  public FeedController(FeedService feedService) {
    this.feedService = feedService;
  }

  /**
   * get user's feed activity(user's own activity + friends' activities).
   *
   * @param principal current user represented by the jwt given
   * @return response with a list of user's activity
   */
  @GetMapping
  @Operation(summary = "Method for getting users' feed activity")
  public ResponseEntity<FeedRsp> getFeed(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    return new ResponseEntity<>(feedService.getFeed(principal.get()), HttpStatus.OK);
  }

}
