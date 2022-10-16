package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.repositories.TransactionRepository;
import com.lgtm.easymoney.services.UserService;
import com.lgtm.easymoney.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService
{
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    @Autowired
    public TransactionServiceImpl(UserService userService, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }
    @Override
    public boolean transactionExists(Transaction t) {
        return t.getId() != null && transactionRepository.existsById(t.getId());
    }
    @Override
    public boolean existsTransactionByID(Long id) {
        return transactionRepository.existsById(id);
    }
    @Override
    public Transaction getTransactionByID(Long id) {
        // TODO wtf is this deprecated
        var transWrapper = transactionRepository.findById(id);
        if (transWrapper.isEmpty()) {
            throw new ResourceNotFoundException("Transaction", "id", id);
        }
        return transWrapper.get();
    }
    @Override
    public Transaction saveTransaction(Transaction t){
        return transactionRepository.save(t);
    }
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    @Override
    public List<Transaction> getAllTransactionsWithUser(User user) {
        return transactionRepository.findByFromOrTo(user, user);
    }
    @Override
    public boolean executeTransaction(Transaction t) {
        if (!transactionExists(t) ||
                !userService.existsByID(t.getFrom().getId()) ||
                !userService.existsByID(t.getTo().getId())) {
            return false;
        }
        if (t.getStatus() == TransactionStatus.REQ_PENDING) {
            User sender = t.getFrom();
            User receiver = t.getTo();
            if (sender.getBalance().compareTo(t.getAmount()) < 0) {
                // TODO insufficient fund, do nothing
                return false;
            }
            sender.setBalance(sender.getBalance().subtract(t.getAmount()));
            receiver.setBalance(receiver.getBalance().add(t.getAmount()));
            userService.saveUser(sender);
            userService.saveUser(receiver);
            // update status
            t.setStatus(TransactionStatus.REQ_COMPLETE);
            // save
            saveTransaction(t);
            return true;
        }
        // not supported transaction status to execute
        return false;

    }
    @Override
    public TransactionRsp generateResponseFromTransaction(Transaction t) {
        TransactionRsp r = new TransactionRsp();
        r.setFromUid(t.getFrom().getId());
        r.setToUid(t.getTo().getId());
        r.setAmount(t.getAmount());
        r.setStatus(t.getStatus());
        r.setDesc(t.getDescription());
        r.setCategory(t.getCategory());
        r.setLastUpdateTime(t.getLastUpdateTime());
        return r;
    }
    @Override
    public List<TransactionRsp> generateListResponseFromTransactions(List<Transaction> l) {
        List<TransactionRsp> res = new ArrayList<>();
        for (Transaction t : l) {
            res.add(generateResponseFromTransaction(t));
        }
        return res;
    }
}
