package com.lgtm.easymoney.services;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("a@a.com");
        user1.setPassword("a");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("b@b.com");
        user2.setPassword("b");

        Mockito.when(userRepository
                .findById(user1.getId())).thenReturn(Optional.of(user1));

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        Mockito.when(userRepository.findAll()).thenReturn(users);
    }

    @Test
    public void shouldGetUserByID() {
        Long id = 1L;
        User user = userService.getUserByID(id);
        Assert.assertEquals(user.getId(), id);
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        Long id = 3L;
        Assert.assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByID(id);
        });
    }

    @Test
    public void shouldGetAllUsers() {
        List<User> users = userService.getAllUsers();
        Assert.assertEquals(users.size(), 2);
    }
}
