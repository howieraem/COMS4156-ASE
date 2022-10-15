package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/request")
public class RequestController {
    private final UserService userService;
    private final RequestService requestService;

    @Autowired
    public RequestController(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @PostMapping("/create")
    // make a new request
    public ResponseEntity<BalanceRsp> createARequest(@Valid @RequestBody RequestReq req) {
        return requestService.createARequest(req);
    }
    // TODO handle other request routes

}
