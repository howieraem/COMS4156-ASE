package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.SearchRsp;

import java.util.List;
import org.springframework.http.ResponseEntity;


public interface SearchService {
    User getUserByID(Long id);
    List<User> getUserByName(String accountName);
    List<User> getUserByEmailOrPhone(String email, String phone);
    ResponseEntity<SearchRsp> searchByID(Long id);
    ResponseEntity<SearchRsp> searchByInfo(String userInfo);
}
