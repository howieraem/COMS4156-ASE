package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.TransferReq;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.payload.rsp.TransferRsp;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.TransferService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Transfer service implementation.
 */
@Service
public class TransferServiceImpl implements TransferService {
  private final TransactionService transactionService;
  private final UserService userService;

  @Autowired
  public TransferServiceImpl(TransactionService transactionService, UserService userService) {
    this.transactionService = transactionService;
    this.userService = userService;
  }

  /**
   * Create a Transaction object and save it to database.
   *
   * @param fromUser the user that money is transferred from
   * @param toUser the user that money is transferred to
   * @param amount the amount of money transferred
   * @param category the category that the money is used for
   * @param desc description of the money transfer
   * @return the created Transaction object
   */
  private Transaction createTransaction(
          User fromUser,
          User toUser,
          BigDecimal amount,
          Category category,
          String desc) {
    Transaction transaction = new Transaction();
    transaction.setFrom(fromUser);
    transaction.setTo(toUser);
    transaction.setAmount(amount);
    transaction.setCategory(category);
    transaction.setDescription(desc);
    transaction.setStatus(TransactionStatus.TRANS_PENDING);
    return transactionService.saveTransaction(transaction);
  }

  /**
   * Execute the transfer transaction.
   */
  private boolean makeTransfer(Transaction transaction) {
    return transactionService.executeTransaction(transaction);
  }

  /**
   * Make a transfer.
   *
   * @param fromUser current logged-in user.
   * @param req transfer request.
   * @return resource created response containing created transfer id.
   */
  @Override
  public ResourceCreatedRsp makeTransfer(User fromUser, TransferReq req) {
    // get params
    Long toUid = req.getToUid();
    BigDecimal amount = req.getAmount();
    String category = req.getCategory();
    String desc = req.getDescription();
    // account validation is currently eliminated because account is guaranteed to exist
    // make a transfer
    User toUser = userService.getUserById(toUid);
    Transaction transaction =
            createTransaction(
                    fromUser,
                    toUser,
                    amount,
                    Category.valueOf(category.toUpperCase()),
                    desc);
    boolean success = makeTransfer(transaction);
    // payload
    if (!success) {
      transaction.setStatus(TransactionStatus.TRANS_FAILED);
      transactionService.saveTransaction(transaction);
      throw new InvalidUpdateException("User", fromUser.getId(),
          "amount", amount);
    }
    return new ResourceCreatedRsp(transaction.getId());
  }

  /**
   * Get the transfer transactions by user id.
   *
   * @param user current logged-in user
   * @return transfer response
   */
  @Override
  public TransferRsp getTransfers(User user) {
    List<TransactionStatus> status = List.of(TransactionStatus.TRANS_COMPLETE);
    List<Transaction> transfers = transactionService.getAllTransactionsWithUser(user, status);
    // payload
    TransferRsp response = new TransferRsp();
    response.setSuccess(true);
    response.setCurrBalance(user.getBalance());
    List<TransactionRsp> transferRsps =
            transactionService.generateListResponseFromTransactions(transfers);
    response.setTransfers(transferRsps);
    response.setMessage("User's transfers returned");
    return response;
  }
}
