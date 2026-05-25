package com.granafy.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.granafy.data.local.entity.TransactionEntity;
import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<TransactionEntity> getAllTransactions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransaction(TransactionEntity transaction);

    @Query("DELETE FROM transactions WHERE id = :id")
    void deleteById(Long id);
}
