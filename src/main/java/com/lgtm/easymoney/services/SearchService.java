package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.SearchRsp;
import java.util.List;


/**
 * search service interface.
 */
public interface SearchService {
  User getUserById(Long id);

  List<User> getUserByName(String accountName);

  List<User> getUserByEmailOrPhone(String email, String phone);

  SearchRsp searchById(Long id);

  SearchRsp searchByInfo(String userInfo);

}
