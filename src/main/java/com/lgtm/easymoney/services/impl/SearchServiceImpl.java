package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.SearchRsp;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.SearchService;
import com.lgtm.easymoney.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class SearchServiceImpl implements SearchService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public SearchServiceImpl(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }
    
    @Override
    public User getUserByID(Long id) {
        var userWrapper = userRepository.findById(id);
        if (userWrapper.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return userWrapper.get();
    }

    @Override
    public User getUserByName(String accountName) {
        var accountWrapper = accountRepository.findByAccountName(accountName);
        if (accountWrapper.isEmpty()) {
            throw new ResourceNotFoundException("Account", "accountName", accountName);
        }
        User user = accountWrapper.get().getAccountUser();
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        var userWrapper = userRepository.findByEmail(email);
        if (userWrapper.isEmpty()) {
            throw new ResourceNotFoundException("User", "email", email);
        }
        return userWrapper.get();
    }

    @Override
    public User getUserByPhone(String phone) {
        var userWrapper = userRepository.findByPhone(phone);
        if (userWrapper.isEmpty()) {
            throw new ResourceNotFoundException("User", "phone", phone);
        }
        return userWrapper.get();
    }

    @Override
    public ResponseEntity<SearchRsp> search(Long id) {
        //Getting user by id
        User user = getUserByID(id);
        boolean searchResult = true;
 
        //Compose response
        SearchRsp res = new SearchRsp();
        res.setSuccess(searchResult);
        res.setAccountName(user.getAccount().getAccountName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
       
        if (searchResult) {
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @Override
    public ResponseEntity<SearchRsp> search(String userInfo) {
        
        User user;
        
        //regex to check valid phone number
        String phoneRegex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        
        //regex to check valid email pattern
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = p.matcher(userInfo);

        //Parse userInfo
        if(matcher.find()) {
            user = getUserByEmail(userInfo);
        }
        else if(userInfo.matches(phoneRegex)) {
            user = getUserByPhone(userInfo);
        }
        else {
            user = getUserByName(userInfo);
        }

        //Successful search
        boolean searchResult = true;
 
        //Compose response
        SearchRsp res = new SearchRsp();
        res.setSuccess(searchResult);
        res.setAccountName(user.getAccount().getAccountName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
       
        if (searchResult) {
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
