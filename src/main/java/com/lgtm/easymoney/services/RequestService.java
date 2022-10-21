package com.lgtm.easymoney.services;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * request service interface.
 */
public interface RequestService {
  boolean existsRequestById(Long id);

  Transaction getRequestById(Long id);

  Transaction saveRequest(Transaction trans);

  List<Transaction> getRequestByUser(User user);

  List<Transaction> getAllRequests();

  Transaction createRequest(
          User reqBy, User reqTo, BigDecimal amount, String desc, Category category);

  ResponseEntity<RequestRsp> createRequest(RequestReq req);

  boolean validateRequest(Transaction request);

  boolean acceptRequest(Transaction request);

  ResponseEntity<RequestRsp> acceptRequest(Long tid, Long fuid, Long tuid);

  boolean declineRequest(Transaction request);

  ResponseEntity<RequestRsp> declineRequest(Long tid, Long fuid, Long tuid);

  ResponseEntity<RequestRsp> getRequestsByUid(Long uid);
}
