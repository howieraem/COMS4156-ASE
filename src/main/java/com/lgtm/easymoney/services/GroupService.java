package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.*;
import org.springframework.http.ResponseEntity;

public interface GroupService {
    Group getGroupById(Long gid);
    ResponseEntity<ResourceCreatedRsp> createAGroup(CreateGroupReq createGroupReq);
    ResponseEntity<SimpApiRsp> inviteToAGroup(InviteToGroupReq inviteToGroupReq);
    ResponseEntity<SimpApiRsp> leaveAGroup(LeaveGroupReq leaveGroupReq);
    ResponseEntity<GroupRsp> getGroup(Long gid);
    Boolean joinAGroup(Group group, User user);
    Boolean leaveAGroup(Group group, User user);
    boolean isInGroup(Group group, User user);
}
