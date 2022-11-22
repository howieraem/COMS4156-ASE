package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * JPA repo for querying users.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  List<User> findByEmailContainingIgnoreCaseOrPhoneContaining(String email, String phone);

  User findByEmail(String email);
}
