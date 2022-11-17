package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.req.CreateGroupReq;
import com.lgtm.easymoney.payload.req.InviteToGroupReq;
import com.lgtm.easymoney.payload.req.LeaveGroupReq;
import com.lgtm.easymoney.payload.rsp.GroupAdsRsp;
import com.lgtm.easymoney.payload.rsp.GroupRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller for handling group related requests.
 */
@RestController
@RequestMapping("/group")
@SecurityRequirement(name = "Authorization")
public class GroupController {
  final GroupService groupService;

  @Autowired
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  /** Create a new group. */
  @PostMapping("/create")
  @Operation(summary = "Method for new group creation.")
  public ResponseEntity<ResourceCreatedRsp> createGroup(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody CreateGroupReq createGroupReq) {
    return new ResponseEntity<>(
        groupService.createGroup(principal.get(), createGroupReq), HttpStatus.CREATED);
  }

  /** Invite a user to a group by a group member. */
  @PutMapping("/invite")
  @Operation(summary = "Method for a user to invite another user to a group.")
  public ResponseEntity<Void> inviteToGroup(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody InviteToGroupReq inviteToGroupReq) {
    groupService.inviteToGroup(principal.get(), inviteToGroupReq);
    return ResponseEntity.ok().build();
  }

  /** Let a user leave a group. */
  @PutMapping("/leave")
  @Operation(summary = "Method for a user to leave a group.")
  public ResponseEntity<Void> leaveGroup(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody LeaveGroupReq leaveGroupReq) {
    groupService.leaveGroup(principal.get(), leaveGroupReq);
    return ResponseEntity.ok().build();
  }

  /** Get a group's name, description and member user ids. */
  @GetMapping("/{id}")
  @Operation(summary =
      "Method to get a group's name, description and the list of user IDs, by a group ID.")
  public ResponseEntity<GroupRsp> getGroup(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @PathVariable(value = "id") @NotNull Long id) {
    return new ResponseEntity<>(
        groupService.getGroupProfile(principal.get(), id), HttpStatus.OK);
  }

  /** Get a list of ads from the business users in a group. */
  @GetMapping("/{id}/business")
  @Operation(summary =
          "Method to get a group's ads(texts from business profiles"
                  + "), description and the list of user IDs, by a group ID.")
  public ResponseEntity<GroupAdsRsp> getGroupAds(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @PathVariable(value = "id") @NotNull Long id) {
    return new ResponseEntity<>(
        groupService.getGroupAds(principal.get(), id), HttpStatus.OK);
  }
}
