package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.AnalyticRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.services.AnalyticService;
import com.lgtm.easymoney.services.LoanService;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of analytic service.
 */
@Service
public class AnalyticServiceImpl implements AnalyticService {

  private final UserService userService;
  private final TransactionService transactionService;
  private final LoanService loanService;

  /**
   * Constructor for analytic service.
   *
   * @param userService user service
   * @param transactionService transaction service
   * @param loanService loan service
   */
  @Autowired
  public AnalyticServiceImpl(UserService userService,
                             TransactionService transactionService, LoanService loanService) {
    this.userService = userService;
    this.transactionService = transactionService;
    this.loanService = loanService;
  }

  @Override
  public AnalyticRsp getAnalytic(User u) {
    BigDecimal expenditure = BigDecimal.ZERO;
    Map<Category, BigDecimal> report = new EnumMap<>(Category.class);
    AnalyticRsp res = new AnalyticRsp(u);

    List<Transaction> transactions = transactionService.getAllTransactionsWithUser(
            u, List.of(TransactionStatus.TRANS_COMPLETE));
    for (Transaction transaction : transactions) {
      if (transaction.getFrom().equals(u)) {
        BigDecimal exp = transaction.getAmount();
        expenditure = expenditure.add(exp);
        report.put(transaction.getCategory(), exp);
      }
    }

    if (u.getType() == UserType.FINANCIAL) {
      Map<String, TransactionStatus> finance = new HashMap<>();
      for (TransactionRsp rsp : loanService.getLoansByUser(u).getLoans()) {
        String email = userService.getUserById(rsp.getToUid()).getEmail();
        TransactionStatus status = rsp.getStatus();
        finance.put(email, status);
      }
      res.setFinance(finance);
    }

    res.setExpenditure(expenditure);
    res.setReport(report);
    return res;
  }
}
