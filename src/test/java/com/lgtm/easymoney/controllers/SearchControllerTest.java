package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.payload.ProfileRsp;
import com.lgtm.easymoney.payload.SearchRsp;
import com.lgtm.easymoney.services.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SearchController.class)
public class SearchControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SearchService searchService;
    private SearchRsp searchRsp;
    private ProfileRsp profileRsp;
    private List<ProfileRsp> profileList;
    private Long uid = 1L;
    private String accountName = "testAccount";
    private String email = "test@test.com";
    private String address = "testAddress";
    private String phone = "3345103321";
    private UserType userType = UserType.valueOf("PERSONAL"); 


    /**
    * Set up reusable test fixtures.
    * */
    @Before
    public void setup() {
        //Compose user profile response
        profileRsp = new ProfileRsp();
        profileRsp.setUid(uid);
        profileRsp.setAccountName(accountName);
        profileRsp.setAddress(address);
        profileRsp.setEmail(email);
        profileRsp.setPhone(phone);
        profileRsp.setUserType(userType);

        //Compose search response
        searchRsp = new SearchRsp();
        searchRsp.setSuccess(true);
        profileList = new ArrayList<ProfileRsp>();
        profileList.add(profileRsp);
        searchRsp.setUserProfiles(profileList);
    }

    @Test
    public void searchByIdSuccess() throws Exception {
        // Arrange
        Mockito.when(searchService.searchById(uid))
                .thenReturn(ResponseEntity.of(Optional.of(searchRsp)));

        // Act
        ResultActions returnedResponse = getSearchById(uid);

        // Assert
        returnedResponse.andExpectAll(
                status().isOk(),
                jsonPath("$.success").value(true),
                jsonPath("$.userProfiles[0].uid").value(uid),
                jsonPath("$.userProfiles[0].accountName").value(accountName),
                jsonPath("$.userProfiles[0].address").value(address),
                jsonPath("$.userProfiles[0].email").value(email),
                jsonPath("$.userProfiles[0].phone").value(phone),
                jsonPath("$.userProfiles[0].userType").value(String.valueOf(userType)));
    }

    @Test
    public void searchByNameSuccess() throws Exception {
    // Arrange
    Mockito.when(searchService.searchByInfo(accountName))
        .thenReturn(ResponseEntity.of(Optional.of(searchRsp)));

    // Act
    ResultActions returnedResponse = getSearchByInfo(accountName);

    // Assert
    returnedResponse.andExpectAll(
        status().isOk(),
        jsonPath("$.success").value(true),
        jsonPath("$.userProfiles[0].uid").value(uid),
        jsonPath("$.userProfiles[0].accountName").value(accountName),
        jsonPath("$.userProfiles[0].address").value(address),
        jsonPath("$.userProfiles[0].email").value(email),
        jsonPath("$.userProfiles[0].phone").value(phone),
        jsonPath("$.userProfiles[0].userType").value(String.valueOf(userType)));
    }

    @Test
    public void searchByEmailSuccess() throws Exception {
        // Arrange
        Mockito.when(searchService.searchByInfo(email))
                .thenReturn(ResponseEntity.of(Optional.of(searchRsp)));

        // Act
        ResultActions returnedResponse = getSearchByInfo(email);

        // Assert
        returnedResponse.andExpectAll(
                status().isOk(),
                jsonPath("$.success").value(true),
                jsonPath("$.userProfiles[0].uid").value(uid),
                jsonPath("$.userProfiles[0].accountName").value(accountName),
                jsonPath("$.userProfiles[0].address").value(address),
                jsonPath("$.userProfiles[0].email").value(email),
                jsonPath("$.userProfiles[0].phone").value(phone),
                jsonPath("$.userProfiles[0].userType").value(String.valueOf(userType)));
    }

    @Test
    public void searchByPhoneSuccess() throws Exception {
        // Arrange
        Mockito.when(searchService.searchByInfo(phone))
                .thenReturn(ResponseEntity.of(Optional.of(searchRsp)));

        // Act
        ResultActions returnedResponse = getSearchByInfo(phone);

        // Assert
        returnedResponse.andExpectAll(
                status().isOk(),
                jsonPath("$.success").value(true),
                jsonPath("$.userProfiles[0].uid").value(uid),
                jsonPath("$.userProfiles[0].accountName").value(accountName),
                jsonPath("$.userProfiles[0].address").value(address),
                jsonPath("$.userProfiles[0].email").value(email),
                jsonPath("$.userProfiles[0].phone").value(phone),
                jsonPath("$.userProfiles[0].userType").value(String.valueOf(userType)));
    }

    @Test
    public void searchByIdFailedWithInvalidPathVar() throws Exception {
        Mockito.doThrow(new NumberFormatException(null))
                .when(searchService).searchById(null);

        Mockito.when(searchService.searchById(Long.valueOf(-1)))
                .thenThrow(new ResourceNotFoundException("Search", "id", Long.valueOf(-1)));

        getSearchById(null).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.errorFields").value("id")
        );

        getSearchById(Long.valueOf(-1)).andExpectAll(
                status().isNotFound(),
                jsonPath("$.errorFields").value("id")
        );

    }

    private ResultActions getSearchById(Long id) throws Exception {
        return mvc.perform(get("/search/id/{id}", String.valueOf(id))
            .accept(MediaType.APPLICATION_JSON));
    }
    
    private ResultActions getSearchByInfo(String info) throws Exception {
        return mvc.perform(get("/search/info/{info}", info)
            .accept(MediaType.APPLICATION_JSON));
    }
}