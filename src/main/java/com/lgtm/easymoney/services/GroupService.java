package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Group;

public interface GroupService {
    Group getGroupById(Long gid);
    Group searchGroupsByName(String name);
    Group createGroup();
}
