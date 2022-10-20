package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.payload.TransferReq;
import com.lgtm.easymoney.payload.TransferRsp;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.TransferService;
import com.lgtm.easymoney.services.UserService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * transfer service implementation.
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

  private boolean makeTransfer(Transaction transaction) {
    return transactionService.executeTransaction(transaction);
  }

  @Override
  public ResponseEntity<TransferRsp> makeTransfer(TransferReq req) {
    // get params
    Long fromUid = req.getFromUid();
    Long toUid = req.getToUid();
    BigDecimal amount = req.getAmount();
    Category category = req.getCategory();
    String desc = req.getDescription();
    // TODO: add validation to prevent abnormal behavior
    // account validation is currently eliminated because account is guaranteed to exist
    // make a transfer
    User fromUser = userService.getUserById(fromUid);
    User toUser = userService.getUserById(toUid);
    Transaction transaction = createTransaction(fromUser, toUser, amount, category, desc);
    boolean success = makeTransfer(transaction);
    // payload
    TransferRsp response = new TransferRsp();
    response.setSuccess(success);
    response.setCurrBalance(fromUser.getBalance());
    List<TransactionRsp> transferRsps =
            transactionService.generateListResponseFromTransactions(List.of(transaction));
    response.setTransfers(transferRsps);
    if (success) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  private List<Transaction> getTransfersByUser(User user) {
    List<TransactionStatus> status = List.of(TransactionStatus.TRANS_COMPLETE);
    return transactionService.getAllTransactionsWithUser(user, status);
  }



  @Override
  public ResponseEntity<TransferRsp> getTransfersByUid(Long uid) {
    User user = userService.getUserById(uid);
    List<Transaction> transfers = getTransfersByUser(user);
    boolean success = transfers != null;
    // payload
    TransferRsp response = new TransferRsp();
    response.setSuccess(success);
    response.setCurrBalance(user.getBalance());
    List<TransactionRsp> transferRsps =
            transactionService.generateListResponseFromTransactions(transfers);
    response.setTransfers(transferRsps);
    response.setMessage("User's transfers returned");
    if (success) {
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }
}
