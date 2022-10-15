package com.lgtm.easymoney.services;


import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;

import java.util.List;

public interface TransactionService {
    boolean transactionExists(Transaction t);
    boolean existsTransactionByID(Long id);
    Transaction getTransactionByID(Long id);
    Transaction saveTransaction(Transaction t);
    List<Transaction> getAllTransactions();
    boolean executeTransaction(Transaction t);
    List<Transaction> getAllTransactionsWithUser(User user);

}
