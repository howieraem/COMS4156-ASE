package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.CreateGroupReq;
import com.lgtm.easymoney.payload.req.InviteToGroupReq;
import com.lgtm.easymoney.payload.req.LeaveGroupReq;
import com.lgtm.easymoney.payload.rsp.GroupAdsRsp;
import com.lgtm.easymoney.payload.rsp.GroupRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;

/**
 * group service.
 */
public interface GroupService {
  Group getGroupById(Long gid);

  ResourceCreatedRsp createGroup(User creator, CreateGroupReq createGroupReq);

  void inviteToGroup(InviteToGroupReq inviteToGroupReq);

  void leaveGroup(LeaveGroupReq leaveGroupReq);

  void leaveGroup(Group group, User user);

  GroupRsp getGroupProfile(Long gid);

  GroupAdsRsp getGroupAds(Long gid);

  void joinGroup(Group group, User user);

  boolean isInGroup(Group group, User user);
}
