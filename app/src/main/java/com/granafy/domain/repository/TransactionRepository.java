package com.granafy.domain.repository;

import com.granafy.domain.model.Transaction;
import java.util.List;

public interface TransactionRepository {
    List<Transaction> getAllTransactions();
    void saveTransaction(Transaction transaction);
    void deleteTransaction(Long id);
}
