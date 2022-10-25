package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.GroupAdsRsp;
import com.lgtm.easymoney.payload.GroupRsp;
import com.lgtm.easymoney.payload.InviteToGroupReq;
import com.lgtm.easymoney.payload.LeaveGroupReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
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
      @Valid @RequestBody CreateGroupReq createGroupReq) {
    return new ResponseEntity<>(groupService.createGroup(createGroupReq), HttpStatus.CREATED);
  }

  /** Invite a user to a group by a group member. */
  @PutMapping("/invite")
  @Operation(summary = "Method for a user to invite another user to a group.")
  public ResponseEntity<Void> inviteToGroup(@Valid @RequestBody InviteToGroupReq inviteToGroupReq) {
    groupService.inviteToGroup(inviteToGroupReq);
    return ResponseEntity.ok().build();
  }

  /** Let a user leave a group. */
  @PutMapping("/leave")
  @Operation(summary = "Method for a user to leave a group.")
  public ResponseEntity<Void> leaveGroup(@Valid @RequestBody LeaveGroupReq leaveGroupReq) {
    groupService.leaveGroup(leaveGroupReq);
    return ResponseEntity.ok().build();
  }

  /** Get a group's name, description and member user ids. */
  @GetMapping("/{id}")
  @Operation(summary =
      "Method to get a group's name, description and the list of user IDs, by a group ID.")
  public ResponseEntity<GroupRsp> getGroup(@PathVariable(value = "id") @NotNull Long id) {
    return new ResponseEntity<>(groupService.getGroupProfile(id), HttpStatus.OK);
  }

  /** Get a list of ads from the business users in a group. */
  @GetMapping("/{id}/business")
  @Operation(summary =
          "Method to get a group's ads(texts from business profiles"
                  + "), description and the list of user IDs, by a group ID.")
  public ResponseEntity<GroupAdsRsp> getGroupAds(@PathVariable(value = "id") @NotNull Long id) {
    return new ResponseEntity<>(groupService.getGroupAds(id), HttpStatus.OK);
  }
}
