package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.LoanRsp;
import com.lgtm.easymoney.payload.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;

/**
 * Loan service interface.
 */
public interface LoanService {
  ResourceCreatedRsp requestLoan(RequestReq req);

  LoanRsp getLoansByUid(Long uid);

  LoanRsp approveLoan(RequestAcceptDeclineReq req);

  ResourceCreatedRsp declineLoan(RequestAcceptDeclineReq req);
}