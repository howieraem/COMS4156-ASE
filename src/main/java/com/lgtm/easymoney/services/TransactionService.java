package com.lgtm.easymoney.services;


import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.TransactionRsp;
import java.util.List;

/**
 * transaction service interface.
 */
public interface TransactionService {
  boolean transactionExists(Transaction t);

  boolean existsTransactionById(Long id);

  Transaction getTransactionById(Long id);

  Transaction saveTransaction(Transaction t);

  List<Transaction> getAllTransactions();

  boolean executeTransaction(Transaction t);

  List<Transaction> getAllTransactionsWithUser(User user, List<TransactionStatus> status);


  TransactionRsp generateResponseFromTransaction(Transaction t);

  List<TransactionRsp> generateListResponseFromTransactions(List<Transaction> l);

}
