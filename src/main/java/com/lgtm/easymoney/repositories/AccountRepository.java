package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNumberAndRoutingNumber(String number, String routingNumber);
}
