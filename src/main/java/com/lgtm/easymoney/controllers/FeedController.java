package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.FeedRsp;
import com.lgtm.easymoney.payload.FriendshipReq;
import com.lgtm.easymoney.payload.ProfilesRsp;
import com.lgtm.easymoney.services.FeedService;
import com.lgtm.easymoney.services.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *  controller for feed activities
 */
@RestController
@RequestMapping("/feed")
public class FeedController {
  final FeedService feedService;

  @Autowired
  public FeedController(FeedService feedService) {
    this.feedService = feedService;
  }


  @GetMapping("/{uid}")
  @Operation(summary = "Method for getting users' feed activity")
  public ResponseEntity<FeedRsp> getFeedByUid(@PathVariable(value = "uid") @NotNull Long uid) {
    return new ResponseEntity<>(feedService.getFeedByUid(uid), HttpStatus.OK);
  }

}
