package com.lgtm.easymoney.repositories;

import com.lgtm.easymoney.enums.TransactionStatus;
import com.lgtm.easymoney.models.Transaction;
import com.lgtm.easymoney.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("select t from Transaction t where (t.from = ?1 or t.to = ?2) and t.status in ?3")
    List<Transaction> findByFromOrToAndStatusIn(User from, User to, List<TransactionStatus> status);
}
