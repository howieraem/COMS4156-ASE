package com.lgtm.easymoney.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.configs.WebSecurityConfig;
import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.LoanRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.LoanService;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Unit test for LoanController.
 * {@link com.lgtm.easymoney.controllers.LoanController}
 */

@RunWith(SpringRunner.class)
@WebMvcTest(LoanController.class)
@Import({WebSecurityConfig.class})
public class LoanControllerTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private LoanService loanService;

  // We test jwt functionalities in integration tests instead
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private RequestReq requestReq;
  private LoanRsp loanRsp;
  private ResourceCreatedRsp resourceCreatedRsp;
  private TransactionRsp transactionRsp;
  private RequestAcceptDeclineReq requestAcceptDeclineReq;
  private UserPrincipal fromPrincipal = UserTestConfig.PERSON1_PRINCIPAL;
  private UserPrincipal toPrincipal = UserTestConfig.PERSON2_PRINCIPAL;
  private User fromUser = UserTestConfig.PERSON1;
  private User toUser = UserTestConfig.PERSON2;
  private Long fromUid = fromUser.getId();
  private Long toUid = toUser.getId();

  private Long requestId = 1L;
  private BigDecimal amount = BigDecimal.valueOf(30.0);
  private String description = "this is a test request";
  private Date lastUpdateTime = new Date(20221020L);

  /**
   * Set up reusable test fixtures.
   */
  @Before
  public void setUp() {
    // request for loan
    requestReq = new RequestReq();
    requestReq.setToUid(toUid);
    requestReq.setAmount(amount);
    requestReq.setCategory("PARTY");
    requestReq.setDescription(description);
    // request for loan approve/decline
    requestAcceptDeclineReq = new RequestAcceptDeclineReq();
    requestAcceptDeclineReq.setToUid(fromUid);
    requestAcceptDeclineReq.setRequestid(requestId);
    // response for loan request
    resourceCreatedRsp = new ResourceCreatedRsp(requestId);
    // transaction response
    transactionRsp = new TransactionRsp(fromUid, toUid, requestId, amount,
        TransactionStatus.LOAN_PENDING, description, Category.PARTY, lastUpdateTime);
    // response for get all loans
    loanRsp = new LoanRsp();
    loanRsp.setLoans(List.of(transactionRsp));
    loanRsp.setMessage("User's loans returned");
  }

  @Test
  public void requestLoanSuccess() throws Exception {
    // Arrange
    Mockito.when(loanService.requestLoan(fromUser, requestReq)).thenReturn(resourceCreatedRsp);
    // Act
    ResultActions returnedResponse = postRequest(requestReq);
    // Assert
    returnedResponse.andExpectAll(
        status().isCreated(),
        jsonPath("$.id").value(1L)
    );
  }

  @Test
  public void requestLoanFailedWithNullToUid() throws Exception {
    // Arrange
    requestReq.setToUid(null);
    // Act
    ResultActions returnedResponse = postRequest(requestReq);
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("toUid")
    );
  }

  @Test
  public void requestLoanFailedWithNegativeAmount() throws Exception {
    // Arrange
    requestReq.setAmount(BigDecimal.valueOf(-100));
    // Act
    ResultActions returnedResponse = postRequest(requestReq);
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("amount")
    );
  }

  @Test
  public void requestLoanFailedWithZeroAmount() throws Exception {
    // Arrange
    requestReq.setAmount(BigDecimal.ZERO);
    // Act
    ResultActions returnedResponse = postRequest(requestReq);
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("amount")
    );
  }

  @Test
  public void requestLoanFailedWithInvalidDecimalAmount() throws Exception {
    // Arrange
    requestReq.setAmount(BigDecimal.valueOf(0.001));
    // Act
    ResultActions returnedResponse = postRequest(requestReq);
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("amount")
    );
  }

  @Test
  public void requestLoanFailedWithNullCategory() throws Exception {
    // Arrange
    requestReq.setCategory(null);
    // Act
    ResultActions returnedResponse = postRequest(requestReq);
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("category")
    );
  }

  @Test
  public void requestLoanFailedWithInvalidCategory() throws Exception {
    // Arrange
    requestReq.setCategory("lunch");
    // Act
    ResultActions returnedResponse = postRequest(requestReq);
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("category")
    );
  }

  @Test
  public void getLoansSuccess() throws Exception {
    // Arrange
    Mockito.when(loanService.getLoansByUser(fromUser)).thenReturn(loanRsp);
    // Act
    ResultActions returnedResponse = mvc.perform(get("/loan").with(user(fromPrincipal)));
    // Assert
    returnedResponse.andExpectAll(
        status().isOk(),
        jsonPath("$.loans[0].fromUid").value(fromUid),
        jsonPath("$.loans[0].toUid").value(toUid),
        jsonPath("$.loans[0].amount").value(amount),
        jsonPath("$.loans[0].status").value("LOAN_PENDING"),
        jsonPath("$.loans[0].desc").value(description),
        jsonPath("$.loans[0].category").value("PARTY")
    );
  }

  @Test
  public void approveLoanSuccess() throws Exception {
    // Arrange
    TransactionRsp transactionRsp1 = new TransactionRsp(toUid, fromUid, 2L, amount,
        TransactionStatus.LOAN_APPROVED, description, Category.PARTY, lastUpdateTime);
    TransactionRsp transactionRsp2 = new TransactionRsp(fromUid, toUid, 3L, amount,
        TransactionStatus.TRANS_PENDING, requestId.toString(), Category.LOAN_PAYBACK,
        lastUpdateTime);
    loanRsp.setLoans(List.of(transactionRsp1, transactionRsp2));
    Mockito.when(loanService.approveLoan(toUser, requestAcceptDeclineReq)).thenReturn(loanRsp);
    // Act
    ResultActions returnedResponse =
        mvc.perform(put("/loan/approve")
            .with(user(toPrincipal))
            .content(asJsonString(requestAcceptDeclineReq))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    // Assert
    returnedResponse.andExpectAll(
        status().isOk(),
        jsonPath("$.loans[0].fromUid").value(toUid),
        jsonPath("$.loans[0].toUid").value(fromUid),
        jsonPath("$.loans[0].amount").value(amount),
        jsonPath("$.loans[0].status").value("LOAN_APPROVED"),
        jsonPath("$.loans[0].desc").value(description),
        jsonPath("$.loans[0].category").value("PARTY"),
        jsonPath("$.loans[1].fromUid").value(fromUid),
        jsonPath("$.loans[1].toUid").value(toUid),
        jsonPath("$.loans[1].amount").value(amount),
        jsonPath("$.loans[1].status").value("TRANS_PENDING"),
        jsonPath("$.loans[1].desc").value(requestId.toString()),
        jsonPath("$.loans[1].category").value("LOAN_PAYBACK")
    );
  }

  @Test
  public void approveLoanFailedWithNullToUid() throws Exception {
    // Arrange
    requestAcceptDeclineReq.setToUid(null);
    // Act
    ResultActions returnedResponse =
        mvc.perform(put("/loan/approve")
            .with(user(toPrincipal))
            .content(asJsonString(requestAcceptDeclineReq))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("toUid")
    );
  }

  @Test
  public void approveLoanFailedWithNullRequestId() throws Exception {
    // Arrange
    requestAcceptDeclineReq.setRequestid(null);
    // Act
    ResultActions returnedResponse =
        mvc.perform(put("/loan/approve")
            .with(user(toPrincipal))
            .content(asJsonString(requestAcceptDeclineReq))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("requestid")
    );
  }

  @Test
  public void declineLoanSuccess() throws Exception {
    // Arrange
    transactionRsp.setFromUid(toUid);
    transactionRsp.setToUid(fromUid);
    transactionRsp.setStatus(TransactionStatus.LOAN_DECLINED);
    loanRsp.setLoans(List.of(transactionRsp));
    Mockito.when(loanService.declineLoan(toUser, requestAcceptDeclineReq)).thenReturn(loanRsp);
    // Act
    ResultActions returnedResponse =
        mvc.perform(put("/loan/decline")
            .with(user(toPrincipal))
            .content(asJsonString(requestAcceptDeclineReq))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    // Assert
    returnedResponse.andExpectAll(
        status().isOk(),
        jsonPath("$.loans[0].fromUid").value(toUid),
        jsonPath("$.loans[0].toUid").value(fromUid),
        jsonPath("$.loans[0].amount").value(amount),
        jsonPath("$.loans[0].status").value("LOAN_DECLINED"),
        jsonPath("$.loans[0].desc").value(description),
        jsonPath("$.loans[0].category").value("PARTY")
    );
  }

  @Test
  public void declineLoanFailedWithNullToUid() throws Exception {
    // Arrange
    requestAcceptDeclineReq.setToUid(null);
    // Act
    ResultActions returnedResponse =
        mvc.perform(put("/loan/decline")
            .with(user(toPrincipal))
            .content(asJsonString(requestAcceptDeclineReq))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("toUid")
    );
  }

  @Test
  public void declineLoanFailedWithNullRequestId() throws Exception {
    // Arrange
    requestAcceptDeclineReq.setRequestid(null);
    // Act
    ResultActions returnedResponse =
        mvc.perform(put("/loan/decline")
            .with(user(toPrincipal))
            .content(asJsonString(requestAcceptDeclineReq))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
    // Assert
    returnedResponse.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("requestid")
    );
  }

  private ResultActions postRequest(RequestReq req) throws Exception {
    return mvc.perform(post("/loan/request")
        .with(user(fromPrincipal))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}