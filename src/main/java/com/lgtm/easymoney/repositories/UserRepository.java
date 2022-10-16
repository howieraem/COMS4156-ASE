package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    List<User> findByEmailContainingIgnoreCaseOrPhoneContaining(String email, String phone);
}
