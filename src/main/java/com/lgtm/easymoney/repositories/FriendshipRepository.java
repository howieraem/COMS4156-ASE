package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.Friendship;
import com.lgtm.easymoney.models.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * friendship repo schema.
 */
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
  List<Friendship> findByUser2(User user2);  // useful for finding friendships not yet accepted

  Friendship findByUser1AndUser2(User user1, User user2);

  void deleteByUser1AndUser2(User user1, User user2);
}
