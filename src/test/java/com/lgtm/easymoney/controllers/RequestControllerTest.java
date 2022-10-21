package com.lgtm.easymoney.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.payload.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.services.RequestService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
  private ResourceCreatedRsp resourceCreatedRsp;
  private TransactionRsp transactionRsp;

  private Long fromUid = 1L;
  private Long toUid = 2L;

  private Long requestId = 1L;
  private BigDecimal amount = BigDecimal.valueOf(30.0);
  private String description = "this is a test request";
  private Date lastUpdateTime = new Date(20221020L);

  /**
   * Set up reusable test fixtures.
   * */
  @Before
  public void setUp() {
    // request for making request
    requestReq = new RequestReq();
    requestReq.setFromUid(fromUid);
    requestReq.setToUid(toUid);
    requestReq.setAmount(amount);
    requestReq.setCategory("PARTY");
    requestReq.setDescription(description);
    // transaction response
    transactionRsp = new TransactionRsp(fromUid, toUid, amount,
            TransactionStatus.TRANS_COMPLETE, description, Category.PARTY, lastUpdateTime);
    // response for making requests
    resourceCreatedRsp = new ResourceCreatedRsp(requestId);
    requestRsp = new RequestRsp();
    requestRsp.setSuccess(true);
    requestRsp.setCurrBalance(BigDecimal.valueOf(70.0));
    requestRsp.setRequests(List.of(transactionRsp));
    // transaction
  }

  @Test
  public void createRequestSuccess() throws Exception {
    // Arrange
    Mockito.when(requestService.createRequest(requestReq)).thenReturn(resourceCreatedRsp);

    // Act
    ResultActions returnedResponse = postRequest(requestReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isCreated(),
            jsonPath("$.id").value(1L));
  }

  @Test
  public void createRequestFailedWithInvalidAmount() throws Exception {
    // negative amount
    requestReq.setAmount(BigDecimal.valueOf(-100));
    postRequest(requestReq).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("amount"));
    // zero
    requestReq.setAmount(BigDecimal.ZERO);
    postRequest(requestReq).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("amount"));
    // too small
    requestReq.setAmount(BigDecimal.valueOf(0.001));
    postRequest(requestReq).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("amount"));

  }

  @Test
  public void createRequestFailedWithInvalidFromUid() throws Exception {
    // fromUid that does not exsist
    requestReq.setFromUid(3L);
    Mockito.when(requestService.createRequest(requestReq))
            .thenThrow(new ResourceNotFoundException("uid", "uid", 3L));
    postRequest(requestReq).andExpectAll(
            status().isNotFound(),
            jsonPath("$.errorFields").value("uid"));
  }

  @Test
  public void createRequestFailedWithInvalidToUid() throws Exception {
    // fromUid that does not exsist
    requestReq.setToUid(3L);
    Mockito.when(requestService.createRequest(requestReq))
            .thenThrow(new ResourceNotFoundException("uid", "uid", 3L));
    postRequest(requestReq).andExpectAll(
            status().isNotFound(),
            jsonPath("$.errorFields").value("uid"));
  }

  @Test
  public void createRequestFailedWithInvalidCategory() throws Exception {
    // null category
    requestReq.setCategory(null);
    postRequest(requestReq).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("category"));
    // empty string
    requestReq.setCategory("");
    postRequest(requestReq).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("category"));
    // string not in Category enum
    requestReq.setCategory("wtf");
    postRequest(requestReq).andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("category"));

  }

  @Test
  public void getRequestsSuccess() throws Exception {
    // Arrange
    Mockito.when(requestService.getRequestsByUid(fromUid)).thenReturn(requestRsp);

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

  @Test
  public void acceptRequestSuccess() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.acceptRequest(requestId, fromUid, toUid)).thenReturn(resourceCreatedRsp);

    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/accept")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(requestId)
    );
  }

  @Test
  public void acceptRequestFailedByInvalidRequest() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.acceptRequest(requestId, fromUid, toUid))
            .thenThrow(new InvalidUpdateException("request cannot be accepted.", requestId, "request", requestId));
    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/accept")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("request")
    );
  }

  @Test
  public void acceptRequestFailedByInvalidSender() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.acceptRequest(requestId, fromUid, toUid))
            .thenThrow(new ResourceNotFoundException("From Uid", "fromUid", fromUid));
    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/accept")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isNotFound(),
            jsonPath("$.errorFields").value("fromUid")
    );
  }

  @Test
  public void acceptRequestFailedByInvalidReceiver() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.acceptRequest(requestId, fromUid, toUid))
            .thenThrow(new ResourceNotFoundException("To Uid", "toUid", toUid));
    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/accept")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isNotFound(),
            jsonPath("$.errorFields").value("toUid")
    );
  }

  @Test
  public void declineRequestSuccess() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.canAcceptDeclineRequest(requestId, fromUid, toUid)).thenReturn(Boolean.TRUE);
    Mockito.when(requestService.declineRequest(Mockito.any(Transaction.class))).thenReturn(Boolean.TRUE);
    Mockito.when(requestService.declineRequest(requestId, fromUid, toUid)).thenReturn(resourceCreatedRsp);

    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/decline")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(requestId)
    );
  }

  @Test
  public void declineRequestFailedByInvalidRequest() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.declineRequest(requestId, fromUid, toUid))
            .thenThrow(new InvalidUpdateException("request cannot be declined.", requestId, "request", requestId));
    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/decline")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("request")
    );
  }

  @Test
  public void declineRequestFailedByInvalidSender() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.declineRequest(requestId, fromUid, toUid))
            .thenThrow(new ResourceNotFoundException("From Uid", "fromUid", fromUid));
    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/decline")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isNotFound(),
            jsonPath("$.errorFields").value("fromUid")
    );
  }

  @Test
  public void declineRequestFailedByInvalidReceiver() throws Exception {
    // Arrange
    RequestAcceptDeclineReq req = new RequestAcceptDeclineReq();
    req.setFromUid(fromUid);
    req.setToUid(toUid);
    req.setRequestid(requestId);
    Mockito.when(requestService.declineRequest(requestId, fromUid, toUid))
            .thenThrow(new ResourceNotFoundException("To Uid", "toUid", toUid));
    // Act
    ResultActions returnedResponse =
            mvc.perform(put("/request/decline")
                    .content(asJsonString(req))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));

    // Assert
    returnedResponse.andExpectAll(
            status().isNotFound(),
            jsonPath("$.errorFields").value("toUid")
    );
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