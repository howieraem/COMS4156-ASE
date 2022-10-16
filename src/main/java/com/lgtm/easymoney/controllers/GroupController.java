package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.payload.*;
import com.lgtm.easymoney.services.GroupService;
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
    public ResponseEntity<ResourceCreatedRsp> createAGroup(@Valid @RequestBody CreateGroupReq createGroupReq) {
        return groupService.createAGroup(createGroupReq);
    }

    @PutMapping("/invite")
    public ResponseEntity<SimpApiRsp> inviteToAGroup(@Valid @RequestBody InviteToGroupReq inviteToGroupReq) {
        return groupService.inviteToAGroup(inviteToGroupReq);
    }

    @PutMapping("/leave")
    public ResponseEntity<SimpApiRsp> leaveAGroup(@Valid @RequestBody LeaveGroupReq leaveGroupReq) {
        return groupService.leaveAGroup(leaveGroupReq);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupRsp> getAGroup(@PathVariable(value = "id") @NotNull Long id) {
        return groupService.getGroup(id);
    }
}
