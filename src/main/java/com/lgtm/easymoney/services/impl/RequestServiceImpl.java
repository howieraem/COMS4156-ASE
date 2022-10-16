package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import com.lgtm.easymoney.payload.TransactionRsp;
import com.lgtm.easymoney.services.TransactionService;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
    public ResponseEntity<RequestRsp> getRequestsByUser(User user) {
        // get params
//        Long uid = user.getId();
        // payload
        RequestRsp res = new RequestRsp();
        List<Transaction> listTrans = getRequestByUser(user);
        res.setSuccess(listTrans != null);
        res.setCurrBalance(user.getBalance());

        res.setRequests(transactionService.generateListResponseFromTransactions(listTrans));
        res.setMessage("Retrieved user's requests!");
        if (listTrans != null) {
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        // TODO refactor this?
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @Override
    public List<Transaction> getAllRequests() {
        return transactionService.getAllTransactions();
    }
    @Override
    public Transaction createARequest(User reqBy, User reqTo, BigDecimal amount, String desc,
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

        return transactionService.saveTransaction(trans);
    }
    @Override
    public ResponseEntity<RequestRsp> createARequest(RequestReq req) {
        // get params
        Long from_uid = req.getFromUid();      // from = req by
        Long to_uid = req.getToUid();          // to = req to
        BigDecimal amount = req.getAmount();
        Category category = req.getCategory();
        String desc = req.getDescription();
        // create a request
        User from_user = userService.getUserByID(from_uid);
        User to_user = userService.getUserByID(to_uid);
        Transaction trans = createARequest(from_user, to_user, amount, desc, category);
        // payload
        // todo need to refactor some of these
        RequestRsp res = new RequestRsp();
        res.setSuccess(trans != null);
        res.setCurrBalance(from_user.getBalance());
        res.setRequests(transactionService.generateListResponseFromTransactions(new ArrayList<>(Arrays.asList(trans))));
        res.setMessage("Request has been created successfully!");
        if (trans != null) {
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

    @Override
    public ResponseEntity<RequestRsp> acceptRequest(Long tid, Long fUid, Long tUid) {
        // verify
        boolean valid = existsRequestByID(tid) &&
                        userService.existsByID(fUid) &&
                        userService.existsByID(tUid) &&
                        getRequestByID(tid).getFrom().getId() == fUid &&
                        getRequestByID(tid).getTo().getId() == tUid &&
                        getRequestByID(tid).getStatus() == TransactionStatus.REQ_PENDING;

        // find request
        Transaction trans = getRequestByID(tid);
        // accept request
        acceptRequest(trans);
        // payload
        RequestRsp res = new RequestRsp();
        if (!valid) {
            res.setSuccess(false);
            res.setMessage("Invalid params for accepting request.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        res.setSuccess(trans != null);
        res.setCurrBalance(userService.getUserByID(fUid).getBalance());
        res.setRequests(transactionService.generateListResponseFromTransactions(new ArrayList<>(Arrays.asList(trans))));
        res.setMessage("Successfully accepted request!");
        if (trans != null) {
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        // TODO refactor this?
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @Override
    public ResponseEntity<RequestRsp> declineRequest(Long tid, Long fUid, Long tUid) {
        // verify
        boolean valid = existsRequestByID(tid) &&
                userService.existsByID(fUid) &&
                userService.existsByID(tUid) &&
                getRequestByID(tid).getFrom().getId() == fUid &&
                getRequestByID(tid).getTo().getId() == tUid &&
                getRequestByID(tid).getStatus() == TransactionStatus.REQ_PENDING;

        // find request
        Transaction trans = getRequestByID(tid);
        // decline request
        declineRequest(trans);
        // payload
        RequestRsp res = new RequestRsp();
        // todo we should use refactor this into exception handler
        if (!valid) {
            res.setSuccess(false);
            StringBuilder msg = new StringBuilder("Invalid params for declining request. ");
            if (trans.getStatus() == TransactionStatus.REQ_DENIED) {
                msg.append("Request is already declined.");
            } else if (trans.getStatus() == TransactionStatus.REQ_COMPLETE) {
                msg.append("Request is already completed. ");
            }
            res.setMessage(msg.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        res.setSuccess(trans != null);
        res.setCurrBalance(userService.getUserByID(fUid).getBalance());
        res.setRequests(transactionService.generateListResponseFromTransactions(new ArrayList<>(Arrays.asList(trans))));
        res.setMessage("Successfully declined request!");
        if (trans != null) {
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        // TODO refactor this?
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
}
