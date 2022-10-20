package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.SearchRsp;
import java.util.List;
import org.springframework.http.ResponseEntity;


/**
 * search service interface.
 */
public interface SearchService {
  User getUserById(Long id);

  List<User> getUserByName(String accountName);

  List<User> getUserByEmailOrPhone(String email, String phone);

  ResponseEntity<SearchRsp> searchById(Long id);

  ResponseEntity<SearchRsp> searchByInfo(String userInfo);

}
