package com.example.transactionspringboot.Service;

import com.example.transactionspringboot.TranEntity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transaction> getAllTransactions(int amount, String description, Date transactionDate) {
        String sql = "SELECT * FROM transactions WHERE amount = ? AND description = ? AND transaction_date = ?";
        return jdbcTemplate.query(sql, new Object[]{amount, description, transactionDate}, new BeanPropertyRowMapper<>(Transaction.class));
    }
}
