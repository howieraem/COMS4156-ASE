package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.RequestAcceptDeclineReq;
import com.lgtm.easymoney.payload.RequestReq;
import com.lgtm.easymoney.payload.RequestRsp;
import com.lgtm.easymoney.services.RequestService;
import com.lgtm.easymoney.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

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
    @Operation(description = "Method for a user to create a money request to another user.")
    public ResponseEntity<RequestRsp> createARequest(@Valid @RequestBody RequestReq req) {
        return requestService.createARequest(req);
    }

    @GetMapping("/{uid}")
    @Operation(description = "Method for a user to get all money requests sent (but not yet completed).")
    public ResponseEntity<RequestRsp> getRequests(@PathVariable(value="uid") Long uid) {
        // get all requests that are sent/received by user
        // param: uid (from/to are the same for now) we may add filtering features later
        // return: list of requests
        return requestService.getRequestsByUser(userService.getUserByID(uid));
    }


    @PutMapping("/accept")
    @Operation(description = "Method for a user to accept a money request from another user.")
    public ResponseEntity<RequestRsp> acceptRequest(@Valid @RequestBody RequestAcceptDeclineReq r) {
        return requestService.acceptRequest(r.getRequestID(),r.getFromUid(),r.getToUid());
    }

    @PutMapping("/decline")
    @Operation(description = "Method for a user to decline a money request from another user.")
    public ResponseEntity<RequestRsp> declineRequest(@Valid @RequestBody RequestAcceptDeclineReq r) {
        return requestService.declineRequest(r.getRequestID(), r.getFromUid(), r.getToUid());
    }
}
