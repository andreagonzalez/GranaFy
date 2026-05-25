package com.granafy.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.granafy.domain.model.Transaction;
import com.granafy.domain.model.TransactionType;
import java.time.LocalDateTime;

@Entity(tableName = "transactions")
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String title;
    private double amount;
    private LocalDateTime date;
    private TransactionType type;
    private String category;
    private String description;

    public TransactionEntity(Long id, String title, double amount, LocalDateTime date, TransactionType type, String category, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    // Getters e Setters (Necessários para o Room)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Métodos de mapeamento para converter entre Domínio e Data
    public Transaction toDomain() {
        return new Transaction(id, title, amount, date, type, category, description);
    }

    public static TransactionEntity fromDomain(Transaction transaction) {
        return new TransactionEntity(
                transaction.getId(),
                transaction.getTitle(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDescription()
        );
    }
}
