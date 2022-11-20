package com.lgtm.easymoney.controllers;

import com.lgtm.easymoney.payload.req.FriendshipReq;
import com.lgtm.easymoney.payload.rsp.ProfilesRsp;
import com.lgtm.easymoney.security.CurrentUser;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "Authorization")
public class FriendController {
  final FriendService friendService;

  @Autowired
  public FriendController(FriendService friendService) {
    this.friendService = friendService;
  }

  /**
   * Method for a user to add a friend (request a friendship, need acceptance).
   *
   * @param principal current logged-in user.
   * @param req Friendship request that contains two user ids.
   * @return 201 Created; A friendship is created.
   */
  @PostMapping("/add")
  @Operation(summary =
      "Method for a user to add a friend (request a friendship, need acceptance).")
  public ResponseEntity<String> addFriend(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody FriendshipReq req) {
    friendService.addFriend(principal.get(), req);
    return new ResponseEntity<>("", HttpStatus.CREATED);
  }

  /**
   * Method for a user to accept a friendship.
   *
   * @param principal current logged-in user.
   * @param req Friendship request that contains two user ids.
   * @return 200 OK status; friend request accepted; the active field of friendship is true.
   */
  @PutMapping("/accept")
  @Operation(summary = "Method for a user to accept a friendship.")
  public ResponseEntity<Void> acceptFriend(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @Valid @RequestBody FriendshipReq req) {
    friendService.acceptFriend(principal.get(), req);
    return ResponseEntity.ok().build();
  }

  /**
   * Method for a user to delete a friend. Since OpenAPI 3.0, it is standard to
   * use path variable(s) rather than request body for DELETE methods.
   *
   * @param principal current logged-in user.
   * @param uid the other user's ID.
   * @return 200 OK.
   */
  @DeleteMapping("/{uid}")
  @Operation(summary = "Method for a user to delete a friend.")
  public ResponseEntity<Void> delFriend(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal,
      @PathVariable(value = "uid") @NotNull Long uid) {
    friendService.delFriend(principal.get(), uid);
    return ResponseEntity.ok().build();
  }

  /**
   * Method for a user to get all friends accepted.
   *
   * @param principal current logged-in user.
   * @return A list of user profiles of the user's accepted friends.
   */
  @GetMapping
  @Operation(summary = "Method for a user to get all friends accepted.")
  public ResponseEntity<ProfilesRsp> getFriends(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    return new ResponseEntity<>(friendService.getFriendProfiles(principal.get()), HttpStatus.OK);
  }

  /**
   * Method for a user to get other users who sent addFriend and not yet accepted by this user.
   *
   * @param principal current logged-in user.
   * @return A list of user profiles of the user's pending friends.
   */
  @GetMapping("/pending")
  @Operation(summary =
      "Method for a user to get other users who sent addFriend and not yet accepted by this user.")
  public ResponseEntity<ProfilesRsp> getFriendsPending(
      @CurrentUser @Parameter(hidden = true) UserPrincipal principal) {
    return new ResponseEntity<>(
        friendService.getFriendProfilesPending(principal.get()), HttpStatus.OK);
  }
}
