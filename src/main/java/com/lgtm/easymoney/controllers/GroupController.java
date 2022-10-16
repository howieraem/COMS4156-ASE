package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        return null;
    }

    @PutMapping("/invite")
    public ResponseEntity<ResourceCreatedRsp> inviteToAGroup(@Valid @RequestBody CreateGroupReq createGroupReq) {
        return null;
    }

    @PutMapping("/leave")
    public ResponseEntity<ResourceCreatedRsp> leaveAGroup(@Valid @RequestBody CreateGroupReq createGroupReq) {
        return null;
    }
}
