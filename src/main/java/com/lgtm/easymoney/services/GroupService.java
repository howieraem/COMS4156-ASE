package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.GroupRsp;
import com.lgtm.easymoney.payload.InviteToGroupReq;
import com.lgtm.easymoney.payload.LeaveGroupReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.SimpApiRsp;
import org.springframework.http.ResponseEntity;

/**
 * group service.
 */
public interface GroupService {
  Group getGroupById(Long gid);

  ResponseEntity<ResourceCreatedRsp> createGroup(CreateGroupReq createGroupReq);

  Boolean joinGroup(Group group, User user);

  ResponseEntity<SimpApiRsp> inviteToGroup(InviteToGroupReq inviteToGroupReq);

  ResponseEntity<SimpApiRsp> leaveGroup(LeaveGroupReq leaveGroupReq);

  Boolean leaveGroup(Group group, User user);

  ResponseEntity<GroupRsp> getGroup(Long gid);


  boolean isInGroup(Group group, User user);
}
