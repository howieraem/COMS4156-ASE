package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.GroupAdsRsp;
import com.lgtm.easymoney.payload.GroupRsp;
import com.lgtm.easymoney.payload.InviteToGroupReq;
import com.lgtm.easymoney.payload.LeaveGroupReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;

/**
 * group service.
 */
public interface GroupService {
  Group getGroupById(Long gid);

  ResourceCreatedRsp createGroup(CreateGroupReq createGroupReq);

  void inviteToGroup(InviteToGroupReq inviteToGroupReq);

  void leaveGroup(LeaveGroupReq leaveGroupReq);

  void leaveGroup(Group group, User user);

  GroupRsp getGroupProfile(Long gid);

  GroupAdsRsp getGroupAds(Long gid);

  void joinGroup(Group group, User user);



  boolean isInGroup(Group group, User user);
}
