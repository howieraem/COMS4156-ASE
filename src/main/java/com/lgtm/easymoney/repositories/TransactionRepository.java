package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromOrTo(User from, User to);
//    boolean existsByEmail(String email);
}
