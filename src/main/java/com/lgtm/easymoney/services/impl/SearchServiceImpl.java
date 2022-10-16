package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.ProfileRsp;
import com.lgtm.easymoney.payload.SearchRsp;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.SearchService;
import com.lgtm.easymoney.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public List<User> getUserByName(String accountName) {
        var accountWrapper = accountRepository.findByAccountNameContainingIgnoreCase(accountName);
        List<User> userList = new ArrayList<User>();
        for (Account account : accountWrapper) {
            userList.add(account.getAccountUser());
        }
        return userList;
    }

    @Override
    public List<User> getUserByEmailOrPhone(String email, String phone) {
        return userRepository.findByEmailContainingIgnoreCaseOrPhoneContaining(email, phone);
    }

    @Override
    public ResponseEntity<SearchRsp> searchByID(Long id) {
        //Getting user by id
        User user = getUserByID(id);
        boolean searchResult = true;
 
        //Compose response
        List<ProfileRsp> profileList = new ArrayList<ProfileRsp>();
        ProfileRsp res = new ProfileRsp();
        res.setAccountName(user.getAccount().getAccountName());
        res.setEmail(user.getEmail());
        res.setAddress(user.getAddress());
        res.setUserType(user.getType());
        res.setPhone(user.getPhone());
        profileList.add(res);
        SearchRsp searchRes = new SearchRsp();
        searchRes.setSuccess(searchResult);
        searchRes.setUserProfiles(profileList);
        
        //Issue response
        if (searchResult) {
            return ResponseEntity.status(HttpStatus.OK).body(searchRes);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchRes);
    }

    @Override
    public ResponseEntity<SearchRsp> searchByInfo(String userInfo) {
        
        List<User> userList;
        Set<Long> uids = new HashSet<>();
        
        //regex to check valid phone number
//        String phoneRegex = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
//
//        //regex to check valid email pattern
//        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
//        java.util.regex.Matcher matcher = p.matcher(userInfo);
//
//        //Parse userInfo
//        if(matcher.find() || userInfo.matches(phoneRegex)) {
//            userList = getUserByEmailOrPhone(userInfo, userInfo);
//        }
//        else {
//            userList = getUserByName(userInfo);
//        }

        userList = getUserByEmailOrPhone(userInfo, userInfo);
        userList.addAll(getUserByName(userInfo));

        //Successful search
        boolean searchResult = true;

        //Compose profile list
        List<ProfileRsp> profileList = new ArrayList<ProfileRsp>();
        for (User user : userList){
            if (uids.contains(user.getId())) continue;
            ProfileRsp res = new ProfileRsp();
            res.setUid(user.getId());
            res.setAccountName(user.getAccount().getAccountName());
            res.setEmail(user.getEmail());
            res.setAddress(user.getAddress());
            res.setUserType(user.getType());
            res.setPhone(user.getPhone());
            profileList.add(res);
            uids.add(user.getId());
        }
       
        //Insert profile list into search response
        SearchRsp searchRes = new SearchRsp();
        searchRes.setSuccess(searchResult);
        searchRes.setUserProfiles(profileList);
       
        if (searchResult) {
            return ResponseEntity.status(HttpStatus.OK).body(searchRes);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchRes);
    }
}
