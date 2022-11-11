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

  void inviteToGroup(User inviter, InviteToGroupReq inviteToGroupReq);

  void leaveGroup(User user, LeaveGroupReq leaveGroupReq);

  GroupRsp getGroupProfile(User user, Long gid);

  GroupAdsRsp getGroupAds(User user, Long gid);

  boolean isInGroup(Group group, User user);
}
