package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.*;
import org.springframework.http.ResponseEntity;

public interface GroupService {
    Group getGroupById(Long gid);
    ResourceCreatedRsp createAGroup(CreateGroupReq createGroupReq);
    void inviteToAGroup(InviteToGroupReq inviteToGroupReq);
    void leaveAGroup(LeaveGroupReq leaveGroupReq);
    GroupRsp getGroupProfile(Long gid);
    void joinAGroup(Group group, User user);
    void leaveAGroup(Group group, User user);
    boolean isInGroup(Group group, User user);
}
