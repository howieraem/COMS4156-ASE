package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
}
