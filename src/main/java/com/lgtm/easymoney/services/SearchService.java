package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.ProfileRsp;
import com.lgtm.easymoney.payload.rsp.ProfilesRsp;
import java.util.List;


/**
 * search service interface.
 */
public interface SearchService {
  User getUserById(Long id);

  List<User> getUserByName(String accountName);

  List<User> getUserByEmailOrPhone(String email, String phone);

  ProfileRsp searchById(Long id);

  ProfilesRsp searchByInfo(String userInfo);

}
