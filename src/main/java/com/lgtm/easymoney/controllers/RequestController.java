package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
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
    public ResponseEntity<RequestRsp> createARequest(@Valid @RequestBody RequestReq req) {
        return requestService.createARequest(req);
    }

    @GetMapping("/get/{uid}")
    public ResponseEntity<RequestRsp> getRequests(@PathVariable(value="uid") Long uid) {
        // get all requests that are sent/received by user
        // param: uid (from/to are the same for now) we may add filtering features later
        // return: list of requests
        System.out.println("in get!");
        return requestService.getRequestsByUser(userService.getUserByID(uid));
    }


    @GetMapping("/accept")
        public ResponseEntity<RequestRsp> acceptRequest(@RequestParam(name="rid")Long requestID,
                                                        @RequestParam(name="fromUid")Long fromUid,
                                                        @RequestParam(name="toUid")Long toUid) {
        return requestService.acceptRequest(requestID, fromUid, toUid);
    }

    @GetMapping("/decline")
    public ResponseEntity<RequestRsp> declineRequest(@RequestParam(name="rid")Long requestID,
                                                     @RequestParam(name="fromUid")Long fromUid,
                                                     @RequestParam(name="toUid")Long toUid) {
        return requestService.declineRequest(requestID, fromUid, toUid);
    }



}
