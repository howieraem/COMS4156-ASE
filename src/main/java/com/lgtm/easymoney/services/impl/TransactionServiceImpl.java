package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import com.lgtm.easymoney.repositories.TransactionRepository;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * transaction service implementation.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
  private final UserService userService;
  private final TransactionRepository transactionRepository;

  // initialize service with dependent UserService and repository.
  @Autowired
  public TransactionServiceImpl(
          UserService userService, TransactionRepository transactionRepository) {
    this.userService = userService;
    this.transactionRepository = transactionRepository;
  }

  /**
   * check if a transaction exists or not.
   *
   * @param t transaction
   * @return boolean, exists or not
   */
  @Override
  public boolean transactionExists(Transaction t) {
    return transactionRepository.existsById(t.getId());
  }

  /**
   * check if a transaction exists or not given id.
   *
   * @param id Long
   * @return boolean, exists or not
   */
  @Override
  public boolean existsTransactionById(Long id) {
    return transactionRepository.existsById(id);
  }

  /**
   * get a transaction by id.
   *
   * @param id transaction id
   * @return transaction
   */
  @Override
  public Transaction getTransactionById(Long id) {
    // wrapper to handle corner case
    var transWrapper = transactionRepository.findById(id);
    if (transWrapper.isEmpty()) {
      throw new ResourceNotFoundException("Transaction", "id", id);
    }
    return transWrapper.get();
  }

  /**
   * save a transaction to DB.
   *
   * @param t transaction
   * @return transaction that was successfully written
   */
  @Override
  public Transaction saveTransaction(Transaction t) {
    return transactionRepository.save(t);
  }

  /**
   * get all transactions in the DB.
   *
   * @return list of transactions
   */
  @Override
  public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  /**
   * find transactions that involve a user with specified status.
   * This is useful when e.g. I want to find all failed transactions sent by me.
   *
   *
   * @param user   user to be searched
   * @param status list of status that I want to include
   * @return list of qualified transactions
   */
  @Override
  public List<Transaction> getAllTransactionsWithUser(User user, List<TransactionStatus> status) {
    // todo feature missing: query by from, or
    return transactionRepository.findByFromOrToAndStatusIn(user, user, status);
  }

  /**
   * execute transaction.
   * A transaction means some money goes from A to B. This function
   * does validation work, and execute the transaction and update
   * related tables.
   *
   * @param t transaction
   * @return boolean indicating if execution is successful
   */
  @Override
  public boolean executeTransaction(Transaction t) {
    if (!transactionExists(t)
            || !userService.existsById(t.getFrom().getId())
            || !userService.existsById(t.getTo().getId())) {
      return false;
    }
    if (t.getStatus() == TransactionStatus.TRANS_PENDING) {
      User sender = t.getFrom();
      User receiver = t.getTo();
      if (sender.getId().equals(receiver.getId())) {
        return false;
      }
      if (sender.getBalance().compareTo(t.getAmount()) < 0) {
        return false;
      }
      sender.setBalance(sender.getBalance().subtract(t.getAmount()));
      receiver.setBalance(receiver.getBalance().add(t.getAmount()));
      userService.saveUser(sender);
      userService.saveUser(receiver);
      // update status
      t.setStatus(TransactionStatus.TRANS_COMPLETE);
      // save
      saveTransaction(t);
      return true;
    }
    // not supported transaction status to execute
    return false;

  }

  /**
   * EXTERNAL generate response from a specific transaction.
   *
   * @param t transaction
   * @return payload with transaction detail
   */
  @Override
  public TransactionRsp generateResponseFromTransaction(Transaction t) {
    TransactionRsp r = new TransactionRsp();
    r.setFromUid(t.getFrom().getId());
    r.setToUid(t.getTo().getId());
    r.setTransactionId(t.getId());
    r.setAmount(t.getAmount());
    r.setStatus(t.getStatus());
    r.setDesc(t.getDescription());
    r.setCategory(t.getCategory());
    r.setLastUpdateTime(t.getLastUpdateTime());
    return r;
  }

  /**
   * generate list of responses payload for transactions.
   *
   * @param l list of transactions.
   * @return list of transaction responses.
   */
  @Override
  public List<TransactionRsp> generateListResponseFromTransactions(List<Transaction> l) {
    List<TransactionRsp> res = new ArrayList<>();
    for (Transaction t : l) {
      res.add(generateResponseFromTransaction(t));
    }
    return res;
  }
}
