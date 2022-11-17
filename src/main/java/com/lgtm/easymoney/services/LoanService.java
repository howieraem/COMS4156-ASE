package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.req.RequestReq;
import com.lgtm.easymoney.payload.rsp.LoanRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;

/**
 * Loan service interface.
 */
public interface LoanService {
  ResourceCreatedRsp requestLoan(User borrower, RequestReq req);

  LoanRsp getLoansByUser(User user);

  LoanRsp approveLoan(User lender, RequestAcceptDeclineReq req);

  LoanRsp declineLoan(User lender, RequestAcceptDeclineReq req);
}