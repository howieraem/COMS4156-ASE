package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.Friendship;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.*;
import com.lgtm.easymoney.repositories.FriendshipRepository;
import com.lgtm.easymoney.repositories.UserRepository;

import static org.junit.Assert.*;

import com.lgtm.easymoney.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.BDDMockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
public class FriendServiceImplTest {
    @InjectMocks
    private FriendServiceImpl friendService;
    @Mock
    private UserService userService;
    @Mock
    private FriendshipRepository friendshipRepository;
    private FriendshipReq friendshipReq;
    private ProfileRsp profileRsp;
    private ProfilesRsp profilesRsp;

    private User user1;
    private Long uid1 = 1L;
    private Account account1 = new Account();
    private User user2;
    private Long uid2 = 2L;
    private Account account2 = new Account();
    Friendship f;

    @Before
    public void setUp() {
        user1 = new User();
        user1.setId(uid1);
        user1.setAccount(account1);
        user1.setTypeByStr("personal");
        account1.setAccountName("account1");
        account1.setAccountUser(user1);
        user2 = new User();
        user2.setId(uid2);
        user2.setAccount(account2);
        user2.setTypeByStr("personal");
        account1.setAccountName("account2");
        account1.setAccountUser(user2);

        Mockito.when(userService.getUserById(user1.getId())).thenReturn(user1);
        Mockito.when(userService.getUserById(user2.getId())).thenReturn(user2);

        f = new Friendship(new Friendship.Key(), user1, user2, true);
        friendshipReq = new FriendshipReq();
        friendshipReq.setUid1(uid1);
        friendshipReq.setUid2(uid2);
    }

    @Test
    public void getFriendshipRecordSuccess() {
        //Arrange
        Mockito.when(friendshipRepository.findByUser1AndUser2(user1, user2)).thenReturn(f);

        //Act
        Friendship f1 = friendService.getFriendshipRecord(user1, user2);

        //Assert
        assertEquals(uid1, f1.getUser1().getId());
    }

    @Test
    public void addFriendSuccess() {
        //Arrange
        Mockito.doAnswer(invocation -> {
            ReflectionTestUtils.setField((Friendship) invocation.getArgument(0), "active", true);
            return invocation.getArgument(0);
        }).when(friendshipRepository).save(Mockito.any(Friendship.class));

        //Act
        friendService.addFriend(friendshipReq);
        Friendship friendship = friendshipRepository.save(f);

        //Assert
        assertEquals(user1, friendship.getUser1());
    }

    @Test
    public void addFriendFailedWithNonPersonalUser() {
        //Act
        user1.setTypeByStr("business");

        //Assert
        assertThrows(InapplicableOperationException.class, ()->friendService.addFriend(friendshipReq));
    }

    @Test
    public void addFriendFailedWithNonPersonalUser2() {
        //Act
        user2.setTypeByStr("business");

        //Assert
        assertThrows(InapplicableOperationException.class, ()->friendService.addFriend(friendshipReq));
    }

    @Test
    public void acceptFriendSuccess() {
        //Arrange
        Mockito.doAnswer(invocation -> {
            ReflectionTestUtils.setField((Friendship) invocation.getArgument(0), "active", true);
            return invocation.getArgument(0);
        }).when(friendshipRepository).save(Mockito.any(Friendship.class));
        f.setActive(false);
        f.setUser2(user1);
        f.setUser1(user2);
        Mockito.when(friendshipRepository.findByUser1AndUser2(user2, user1)).thenReturn(f);
        var createdF = new Friendship();
        createdF.setUser1(user1);
        createdF.setUser2(user2);
        Mockito.when(friendshipRepository.findByUser1AndUser2(user1, user2)).thenReturn(createdF);

        //Act
        friendService.acceptFriend(friendshipReq);
        Friendship fs2 = friendshipRepository.findByUser1AndUser2(user2, user1);

        //Assert
        assertTrue(fs2.getActive());
    }

    @Test
    public void acceptFriendFailedWithNonPersonalUser() {
        //Arrange
        f.setActive(false);
        user1.setTypeByStr("business");
        Mockito.when(friendshipRepository.findByUser1AndUser2(user2, user1)).thenReturn(f);

        //Act && Assert
        assertThrows(InapplicableOperationException.class, ()->friendService.acceptFriend(friendshipReq));

    }

    @Test
    public void acceptFriendFailedWithNonPersonalUser2() {
        //Arrange
        f.setActive(false);
        user2.setTypeByStr("business");
        Mockito.when(friendshipRepository.findByUser1AndUser2(user2, user1)).thenReturn(f);

        //Act && Assert
        assertThrows(InapplicableOperationException.class, ()->friendService.acceptFriend(friendshipReq));
    }

    @Test
    public void acceptFriendFailedWithNoFriendship() {
        //Arr
        Mockito.when(friendshipRepository.findByUser1AndUser2(user2, user1)).thenReturn(null);
        //Act && Assert
        assertThrows(ResourceNotFoundException.class, ()->friendService.acceptFriend(friendshipReq));
    }

    @Test
    public void acceptFriendFailedWithActiveFriendship() {
        //Arrange
        Mockito.when(friendshipRepository.findByUser1AndUser2(user2, user1)).thenReturn(f);

        //Act && Assert
        assertThrows(InvalidUpdateException.class, ()->friendService.acceptFriend(friendshipReq));
    }

    @Test
    public void delFriendSuccess() {
        //Arrange
        Mockito.when(friendshipRepository.findByUser1AndUser2(user1, user2)).thenReturn(f);
        var mirrorF = new Friendship();
        mirrorF.setUser1(user2);
        mirrorF.setUser2(user1);
        mirrorF.setActive(true);
        Mockito.when(friendshipRepository.findByUser1AndUser2(user2, user1)).thenReturn(mirrorF);

        //Act
        friendService.delFriend(friendshipReq);

        //Assert
        assertNull(user1.getFriendships());
        assertNull(user2.getFriendships());
    }

    @Test
    public void delFriendFailedWithNoFriendship() {
        //Arrange
        Mockito.when(friendshipRepository.findByUser1AndUser2(user1, user2)).thenReturn(null);

        //Act && Assert
        assertThrows(ResourceNotFoundException.class, ()->friendService.delFriend(friendshipReq));;
    }

    @Test
    public void getFriendsSuccess() {
        //Arrange
        user1.setFriendships(Set.of(f));

        //Act
        List<User> res = friendService.getFriends(user1);

        //Assert
        assertEquals(user2,res.get(0));
    }

    @Test
    public void getFriendsSuccessWithId() {
        //Arrange
        user1.setFriendships(Set.of(f));

        //Act
        ProfilesRsp res = friendService.getFriends(user1.getId());

        //Assert
        assertEquals(user2.getId(),res.getUserProfiles().get(0).getUid());
    }

    @Test
    public void getFriendsPendingSuccess() {
        //Arrange
        f.setActive(false);
        Mockito.when(friendshipRepository.findByUser2(user2)).thenReturn(List.of(f));

        //Act
        ProfilesRsp res = friendService.getFriendsPending(user2.getId());

        //Assert
        assertEquals(user1.getId(),res.getUserProfiles().get(0).getUid());
    }

}
