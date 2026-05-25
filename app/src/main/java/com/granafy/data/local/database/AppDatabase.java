package com.granafy.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.granafy.data.local.dao.TransactionDao;
import com.granafy.data.local.entity.TransactionEntity;

@Database(entities = {TransactionEntity.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract TransactionDao transactionDao();
    
    public static final String DATABASE_NAME = "granafy_db";
}
