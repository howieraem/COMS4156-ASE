package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class RequestServiceImpl implements RequestService {
    private final TransactionService transactionService;
    private final UserService userService;
    @Autowired
    public RequestServiceImpl(TransactionService transactionService, UserService userService){
        this.transactionService = transactionService;
        this.userService = userService;
    }
    @Override
    public boolean existsRequestByID(Long id) {
        return transactionService.existsTransactionByID(id);
    }
    @Override
    public Transaction getRequestByID(Long id) {
        return transactionService.getTransactionByID(id);
    }
    @Override
    public Transaction saveRequest(Transaction trans) {
        return transactionService.saveTransaction(trans);
    }
    @Override
    public List<Transaction> getRequestByUser(User user) {
        return transactionService.getAllTransactionsWithUser(user);
    }
    @Override
    public List<Transaction> getAllRequests() {
        return transactionService.getAllTransactions();
    }
    @Override
    public boolean createARequest(User reqBy, User reqTo, BigDecimal amount, String desc,
                                  Category category) {
        // TODO validate input

        Transaction trans = new Transaction();
        // request: requested by A, requested to B
        // once accept, money goes from B to A
        trans.setFrom(reqTo);
        trans.setTo(reqBy);
        trans.setAmount(amount);
        trans.setDescription(desc);
        trans.setCategory(category);
        trans.setStatus(TransactionStatus.REQ_PENDING);

        transactionService.saveTransaction(trans);
        return true;
    }
    @Override
    public ResponseEntity<BalanceRsp> createARequest(RequestReq req) {
        // get params
        Long from_uid = req.getFrom_uid();      // from = req by
        Long to_uid = req.getTo_uid();          // to = req to
        BigDecimal amount = req.getAmount();
        Category category = req.getCategory();
        String desc = req.getDescription();
        // make a deposit
        User from_user = userService.getUserByID(from_uid);
        User to_user = userService.getUserByID(to_uid);
        boolean success = createARequest(from_user, to_user, amount, desc, category);
        // payload
        BalanceRsp res = new BalanceRsp();
        res.setSuccess(success);
        res.setCurrBalance(from_user.getBalance());
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        // TODO refactor this?
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @Override
    public boolean validateRequest(Transaction request) {
        // TODO maybe it's better to handle exceptions here?
        // TODO validate user?
        // a valid request = valid users, transaction
        // status should be pending, otherwise it's
        // not a valid REQUEST, it's a TRANSFER
        return existsRequestByID(request.getId());
    }
    @Override
    public boolean acceptRequest(Transaction request) {
        // here user is reqTo
        if (!validateRequest(request)) {
            return false;
        }
        // maybe need to refactor this
        boolean success = transactionService.executeTransaction(request);
        return success;

    };
    @Override
    public boolean declineRequest(Transaction request) {
        // here user is reqTo
        if (!validateRequest(request)) {
            return false;
        }
        request.setStatus(TransactionStatus.REQ_DENIED);
        transactionService.saveTransaction(request);
        return true;
    }
}
