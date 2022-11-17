package com.lgtm.easymoney.payload.rsp;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for getting loans.
 */
@Data
@NoArgsConstructor
public class LoanRsp {
  /* request response
  GET all loans by user:
      param: uid, from/to
      return: list of loans
  APPROVE a loan:
      param: from uid, to uid, loan id
      return: loan
  DECLINE a loan:
      param: from uid, to uid, loan id
      return: loan
  */
  private String message;
  private List<TransactionRsp> loans;
}
