package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.SearchRsp;
import org.springframework.http.ResponseEntity;

public interface SearchService {
    User getUserByID(Long id);
    User getUserByName(String accountName);
    User getUserByEmail(String email);
    User getUserByPhone(String phone);
    ResponseEntity<SearchRsp> search(Long id);
    ResponseEntity<SearchRsp> search(String userInfo);
}
