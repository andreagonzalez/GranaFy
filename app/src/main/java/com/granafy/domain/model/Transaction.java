package com.granafy.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private final Long id;
    private final String title;
    private final double amount;
    private final LocalDateTime date;
    private final TransactionType type;
    private final String category;
    private final String description;

    public Transaction(Long id, String title, double amount, LocalDateTime date, TransactionType type, String category, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
    public TransactionType getType() { return type; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(date, that.date) &&
                type == that.type &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, amount, date, type, category);
    }
}
