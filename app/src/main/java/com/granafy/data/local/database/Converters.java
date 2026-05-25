package com.granafy.data.local.database;

import androidx.room.TypeConverter;
import com.granafy.domain.model.TransactionType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converters {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @TypeConverter
    public static LocalDateTime fromTimestamp(String value) {
        return value == null ? null : LocalDateTime.parse(value, formatter);
    }

    @TypeConverter
    public static String dateToTimestamp(LocalDateTime date) {
        return date == null ? null : date.format(formatter);
    }

    @TypeConverter
    public static TransactionType fromString(String value) {
        return value == null ? null : TransactionType.valueOf(value);
    }

    @TypeConverter
    public static String typeToString(TransactionType type) {
        return type == null ? null : type.name();
    }
}
