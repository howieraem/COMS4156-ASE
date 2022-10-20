package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.CreateGroupReq;
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

  @PostMapping("/create")
  @Operation(description = "Method for new group creation.")
  public ResponseEntity<ResourceCreatedRsp> createAGroup(@Valid @RequestBody CreateGroupReq createGroupReq) {
    return new ResponseEntity<>(groupService.createGroup(createGroupReq), HttpStatus.CREATED);
  }

  @PutMapping("/invite")
  @Operation(description = "Method for a user to invite another user to a group.")
  public ResponseEntity<Void> inviteToAGroup(@Valid @RequestBody InviteToGroupReq inviteToGroupReq) {
    groupService.inviteToGroup(inviteToGroupReq);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/leave")
  @Operation(description = "Method for a user to leave a group.")
  public ResponseEntity<Void> leaveAGroup(@Valid @RequestBody LeaveGroupReq leaveGroupReq) {
    groupService.leaveGroup(leaveGroupReq);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  @Operation(description = "Method to get the list of user IDs, the name and the description of a group by a group ID.")
  public ResponseEntity<GroupRsp> getAGroup(@PathVariable(value = "id") @NotNull Long id) {
    return new ResponseEntity<>(groupService.getGroupProfile(id), HttpStatus.OK);
  }
}
