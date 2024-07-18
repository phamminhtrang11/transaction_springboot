package com.example.transactionspringboot.Service;

import com.example.transactionspringboot.TranEntity.Transaction;
import com.example.transactionspringboot.TranEntity.searchReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class TransactionService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public TransactionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public List<Transaction> getAllTransactions(int page, int size) {
        int offset = page * size;
        String sql = "SELECT * FROM transactions LIMIT :limit OFFSET :offset";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", size);
        parameters.addValue("offset", offset);

        return namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(Transaction.class));
    }

    public int addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (amount, description, transaction_date) VALUES (:amount, :description, :transactionDate)";
        Map parameters = new HashMap();
        parameters.put("amount", transaction.getAmount());
        parameters.put("description", transaction.getDescription());
        parameters.put("transactionDate", transaction.getTransactionDate());

        try {
            int insertedTransaction = namedParameterJdbcTemplate.update(sql, parameters);
            return insertedTransaction;
        } catch (EmptyResultDataAccessException e) {
            System.err.println("Transaction not found after insertion: " + e.getMessage());
            return 0;
        }
    }


    public int updateTransaction(Long id, Transaction transaction) {
        String sql = "UPDATE transactions SET amount = :amount, description = :description, transaction_date = :transactionDate WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        parameters.addValue("amount", transaction.getAmount());
        parameters.addValue("description", transaction.getDescription());
        parameters.addValue("transactionDate", transaction.getTransactionDate());

        int updatedRows = namedParameterJdbcTemplate.update(sql, parameters);
        if (updatedRows == 0) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }

        transaction.setId(id);
        return 0;
    }

    public int deleteTransaction(Long id) {
        String sql = "DELETE FROM transactions WHERE id = :id";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        int deletedRows = namedParameterJdbcTemplate.update(sql, parameters);
        if (deletedRows == 0) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        return 0;
    }

    public List<Transaction> search(searchReq req, int page, int size) {
        String sql = "SELECT * FROM transactions";
        StringJoiner where = new StringJoiner(" AND ", " WHERE ", "").setEmptyValue("");
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (req.getMinAmount() != null) {
            where.add("amount >= :minAmount");
            parameters.addValue("minAmount", Long.valueOf(req.getMinAmount()));
        }
        if (req.getMaxAmount() != null) {
            where.add("amount <= :maxAmount");
            parameters.addValue("maxAmount", Long.valueOf(req.getMaxAmount()));
        }
        if (req.getMinDate() != null && req.getMaxDate() != null) {
            where.add("transaction_date BETWEEN :minDate AND :maxDate");
            parameters.addValue("minDate", req.getMinDate());
            parameters.addValue("maxDate", req.getMaxDate());
        }
        if (req.getDescription() != null) {
            where.add("description LIKE :description");
            parameters.addValue("description", "%" + req.getDescription() + "%");
        }

        sql += where.toString();
        sql += " LIMIT :limit OFFSET :offset";
        parameters.addValue("limit", size);
        parameters.addValue("offset", page * size);

        return namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(Transaction.class));
    }
}
