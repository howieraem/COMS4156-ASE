package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.Account;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * repository for accounts.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  List<Account> findByAccountNameContainingIgnoreCase(String accountName);
}