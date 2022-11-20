package com.lgtm.easymoney.payload.rsp;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.User;
import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Analytic Response Payload.
 */
@Data
@NoArgsConstructor
public class AnalyticRsp {
  private Long uid;
  private String accountName;
  private BigDecimal currBalance;
  private BigDecimal expenditure;
  private Map<Category, BigDecimal> report;
  
  // For financial user: key is requester's email, value is loan status
  private Map<String, TransactionStatus> finance;

  /**
   * Constructor for analytic payload.
   *
   * @param u user object
   */
  public AnalyticRsp(User u) {
    this.uid = u.getId();
    this.accountName = u.getAccount().getAccountName();
    this.currBalance = u.getBalance();
  }
}
