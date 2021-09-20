package com.clane.app.wallet.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "select t.* from clane_txn t where (t.sender_no = :phoneNo or t.sender_no = :email) and t.activity = :activity " +
            "and t.txn_type = :txnType and t.txn_date = CURRENT_DATE", nativeQuery = true)
    List<Transaction> findDailyTransaction(@Param("activity") String activity, @Param("phoneNo") String phoneNo,
                                           @Param("email") String email, @Param("txnType") String txnType);
}
