package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.FriendshipReq;
import com.lgtm.easymoney.payload.ProfilesRsp;
import com.lgtm.easymoney.services.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * controller for handling group related requests.
 */
@RestController
@RequestMapping("/friend")
public class FriendController {
  final FriendService friendService;

  @Autowired
  public FriendController(FriendService friendService) {
    this.friendService = friendService;
  }

  /*
   * Method for a user to add a friend (request a friendship, need acceptance)
   * @param req Friendship request that contains two user ids
   * @return 201 Created; A friendship is created
   */
  @PostMapping("/add")
  @Operation(summary =
      "Method for a user to add a friend (request a friendship, need acceptance).")
  public ResponseEntity<Void> addFriend(@Valid @RequestBody FriendshipReq req) {
    friendService.addFriend(req);
    return new ResponseEntity<>(null, HttpStatus.CREATED);
  }

  /*
   * Method for a user to accept a friendship
   * @param req Friendship request that contains two user ids
   * @return 200 OK status; friend request accepted; the active field of friendship is true
   */
  @PutMapping("/accept")
  @Operation(summary = "Method for a user to accept a friendship.")
  public ResponseEntity<Void> acceptFriend(@Valid @RequestBody FriendshipReq req) {
    friendService.acceptFriend(req);
    return ResponseEntity.ok().build();
  }

  /*
   * Method for a user to delete a friend
   * @param req Friendship request that contains two user ids
   * @return 200 OK
   */
  @DeleteMapping("/delete")
  @Operation(summary = "Method for a user to delete a friend.")
  public ResponseEntity<Void> delFriend(@Valid @RequestBody FriendshipReq req) {
    friendService.delFriend(req);
    return ResponseEntity.ok().build();
  }

  /*
   * Method for a user to get all friends accepted
   * @param uid One user id
   * @return A list of user profiles of the user's accepted friends
   */
  @GetMapping("/{uid}")
  @Operation(summary = "Method for a user to get all friends accepted.")
  public ResponseEntity<ProfilesRsp> getFriends(@PathVariable(value = "uid") @NotNull Long uid) {
    return new ResponseEntity<>(friendService.getFriends(uid), HttpStatus.OK);
  }

  /*
   * Method for a user to get other users who sent addFriend and not yet accepted by this user
   * @param uid One user id
   * @return A list of user profiles of the user's pending friends
   */
  @GetMapping("/{uid}/pending")
  @Operation(summary =
      "Method for a user to get other users who sent addFriend and not yet accepted by this user.")
  public ResponseEntity<ProfilesRsp> getFriendsPending(
      @PathVariable(value = "uid") @NotNull Long uid) {
    return new ResponseEntity<>(friendService.getFriendsPending(uid), HttpStatus.OK);
  }
}
