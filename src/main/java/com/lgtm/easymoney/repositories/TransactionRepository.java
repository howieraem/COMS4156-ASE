package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromOrTo(Long from_id, Long to_id);
//    boolean existsByEmail(String email);
}
