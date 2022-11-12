package com.lgtm.easymoney.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.payload.req.TransferReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.payload.rsp.TransferRsp;
import com.lgtm.easymoney.services.TransferService;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Unit test for TransferController.
 * {@link com.lgtm.easymoney.controllers.TransferController}
 * */

@RunWith(SpringRunner.class)
@WebMvcTest(TransferController.class)
public class TransferControllerTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private TransferService transferService;
  private TransferReq transferReq;
  private TransferRsp transferRsp;
  private TransactionRsp transactionRsp;
  private Long fromUid = 1L;
  private Long toUid = 2L;
  private BigDecimal amount = BigDecimal.valueOf(30.0);
  private String description = "this is a test transfer";
  private Date lastUpdateTime = new Date(20221020L);
  private Long transactionId = 11L;

  /**
   * Set up reusable test fixtures.
   * */
  @Before
  public void setUp() {
    // transferReq
    transferReq = new TransferReq();
    transferReq.setFromUid(fromUid);
    transferReq.setToUid(toUid);
    transferReq.setAmount(amount);
    transferReq.setCategory("party");
    transferReq.setDescription(description);
    // transactionRsp
    transactionRsp = new TransactionRsp(fromUid, toUid, amount,
        TransactionStatus.TRANS_COMPLETE, description, Category.PARTY, lastUpdateTime);
    // transferRsp
    transferRsp = new TransferRsp();
    transferRsp.setSuccess(true);
    transferRsp.setCurrBalance(BigDecimal.valueOf(70.0));
    transferRsp.setTransfers(List.of(transactionRsp));
  }

  @Test
  public void transferSuccess() throws Exception {
    // Arrange
    ResourceCreatedRsp resourceCreatedRsp = new ResourceCreatedRsp(transactionId);
    Mockito.when(transferService.makeTransfer(transferReq))
        .thenReturn(resourceCreatedRsp);

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isCreated(),
        jsonPath("$.id").value(transactionId));
  }

  @Test
  public void transferFailedWithNullFromUid() throws Exception {
    // Arrange
    transferReq.setFromUid(null);

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("fromUid"));
  }

  @Test
  public void transferFailedWithNullToUid() throws Exception {
    // Arrange
    transferReq.setToUid(null);

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("toUid"));
  }

  @Test
  public void transferFailedWithNegativeAmount() throws Exception {
    // Arrange
    transferReq.setAmount(BigDecimal.valueOf(-100));

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void transferFailedWithZeroAmount() throws Exception {
    // Arrange
    transferReq.setAmount(BigDecimal.ZERO);

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void transferFailedWithInvalidDecimalAmount() throws Exception {
    // Arrange
    transferReq.setAmount(BigDecimal.valueOf(0.001));

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void transferFailedWithInvalidCategory() throws Exception {
    // Arrange
    transferReq.setCategory("lunch");

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("category"));
  }

  @Test
  public void transferFailedWithEmptyCategory() throws Exception {
    // Arrange
    transferReq.setCategory("");

    // Act
    ResultActions returnedResponse = postTransfer(transferReq);

    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("category"));
  }

  @Test
  public void getTransfersSuccess() throws Exception {
    // Arrange
    Mockito.when(transferService.getTransfersByUid(fromUid)).thenReturn(transferRsp);

    // Act
    ResultActions returnedResponse =
        mvc.perform(get("/transfer/{uid}", String.valueOf(fromUid)));

    // Assert
    returnedResponse.andExpectAll(
        status().isOk(),
        jsonPath("$.success").value(true),
        jsonPath("$.currBalance").value("70.0"),
        jsonPath("$.transfers[0].fromUid").value(fromUid),
        jsonPath("$.transfers[0].toUid").value(toUid),
        jsonPath("$.transfers[0].amount").value(amount),
        jsonPath("$.transfers[0].status").value("TRANS_COMPLETE"),
        jsonPath("$.transfers[0].desc").value(description),
        jsonPath("$.transfers[0].category").value("PARTY"));
  }

  private ResultActions postTransfer(TransferReq transferReq) throws Exception {
    return mvc.perform(post("/transfer/create")
        .content(asJsonString(transferReq))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
