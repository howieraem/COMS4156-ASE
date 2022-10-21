package com.lgtm.easymoney.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Unit test for TransferController.
 * {@link com.lgtm.easymoney.controllers.RequestController}
 * */

@RunWith(SpringRunner.class)
@WebMvcTest(RequestController.class)
@ContextConfiguration
public class RequestControllerTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private RequestService requestService;

  private RequestReq requestReq;
  private RequestRsp requestRsp;
  private TransactionRsp transactionRsp;
  private Long fromUid = 1L;
  private Long toUid = 2L;
  private BigDecimal amount = BigDecimal.valueOf(30.0);
  private String description = "this is a test request";
  private Date lastUpdateTime = new Date(20221020L);

  /**
   * Set up reusable test fixtures.
   * */
  @Before
  public void setUp() {
    requestReq = new RequestReq();
    requestReq.setFromUid(fromUid);
    requestReq.setToUid(toUid);
    requestReq.setAmount(amount);
    requestReq.setCategory(Category.PARTY);
    requestReq.setDescription(description);

    transactionRsp = new TransactionRsp(fromUid, toUid, amount,
            TransactionStatus.TRANS_COMPLETE, description, Category.PARTY, lastUpdateTime);

    requestRsp = new RequestRsp();
    requestRsp.setSuccess(true);
    requestRsp.setCurrBalance(BigDecimal.valueOf(70.0));
    requestRsp.setRequests(List.of(transactionRsp));
  }

  @Test
  public void requestSuccess() throws Exception {
    // Arrange
    Mockito.when(requestService.createRequest(requestReq))
            .thenReturn(ResponseEntity.of(Optional.of(requestRsp)));

    // Act
    ResultActions returnedResponse = postRequest(requestReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.success").value(true),
            jsonPath("$.currBalance").value("70.0"),
            jsonPath("$.requests[0].fromUid").value(fromUid),
            jsonPath("$.requests[0].toUid").value(toUid),
            jsonPath("$.requests[0].amount").value(amount),
            jsonPath("$.requests[0].status").value("TRANS_COMPLETE"),
            jsonPath("$.requests[0].desc").value(description),
            jsonPath("$.requests[0].category").value("PARTY"));
  }

  @Test
  public void requestFailedWithNegativeAmount() throws Exception {
    // Arrange
    requestReq.setAmount(BigDecimal.valueOf(-100));
    Mockito.when(requestService.createRequest(requestReq))
            .thenReturn(ResponseEntity.of(Optional.of(requestRsp)));

    // Act
    ResultActions returnedResponse = postRequest(requestReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void requestFailedWithZeroAmount() throws Exception {
    // Arrange
    requestReq.setAmount(BigDecimal.ZERO);
    Mockito.when(requestService.createRequest(requestReq))
            .thenReturn(ResponseEntity.of(Optional.of(requestRsp)));

    // Act
    ResultActions returnedResponse = postRequest(requestReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void requestFailedWithInvalidDecimalAmount() throws Exception {
    // Arrange
    requestReq.setAmount(BigDecimal.valueOf(0.001));
    Mockito.when(requestService.createRequest(requestReq))
            .thenReturn(ResponseEntity.of(Optional.of(requestRsp)));

    // Act
    ResultActions returnedResponse = postRequest(requestReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void getRequestsSuccess() throws Exception {
    // Arrange
    Mockito.when(requestService.getRequestsByUid(fromUid))
            .thenReturn(ResponseEntity.of(Optional.of(requestRsp)));

    // Act
    ResultActions returnedResponse =
            mvc.perform(get("/request/{uid}", String.valueOf(fromUid)));

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.success").value(true),
            jsonPath("$.currBalance").value("70.0"),
            jsonPath("$.requests[0].fromUid").value(fromUid),
            jsonPath("$.requests[0].toUid").value(toUid),
            jsonPath("$.requests[0].amount").value(amount),
            jsonPath("$.requests[0].status").value("TRANS_COMPLETE"),
            jsonPath("$.requests[0].desc").value(description),
            jsonPath("$.requests[0].category").value("PARTY"));
  }

  private ResultActions postRequest(RequestReq req) throws Exception {
    return mvc.perform(post("/request/create")
            .content(asJsonString(req))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
  }

  private String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
