package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.payload.*;
import com.lgtm.easymoney.services.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
        return new ResponseEntity<>(groupService.createAGroup(createGroupReq), HttpStatus.CREATED);
    }

    @PutMapping("/invite")
    @Operation(description = "Method for a user to invite another user to a group.")
    public ResponseEntity<Void> inviteToAGroup(@Valid @RequestBody InviteToGroupReq inviteToGroupReq) {
        groupService.inviteToAGroup(inviteToGroupReq);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/leave")
    @Operation(description = "Method for a user to leave a group.")
    public ResponseEntity<Void> leaveAGroup(@Valid @RequestBody LeaveGroupReq leaveGroupReq) {
        groupService.leaveAGroup(leaveGroupReq);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(description = "Method to get the list of user IDs, the name and the description of a group by a group ID.")
    public ResponseEntity<GroupRsp> getAGroup(@PathVariable(value = "id") @NotNull Long id) {
        return new ResponseEntity<>(groupService.getGroupProfile(id), HttpStatus.OK);
    }
}
