package com.lgtm.easymoney.services;

import com.lgtm.easymoney.payload.req.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.LoanRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;

/**
 * Loan service interface.
 */
public interface LoanService {
  ResourceCreatedRsp requestLoan(RequestReq req);

  LoanRsp getLoansByUid(Long uid);

  LoanRsp approveLoan(RequestAcceptDeclineReq req);

  LoanRsp declineLoan(RequestAcceptDeclineReq req);
}