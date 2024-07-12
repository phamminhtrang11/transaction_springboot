package com.example.transactionspringboot.Service;

import com.example.transactionspringboot.TranEntity.Transaction;
import com.example.transactionspringboot.TranEntity.searchReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Transaction> getAllTransactions() {
        String sql = "SELECT * FROM transactions";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Transaction.class));
    }

    public List<Transaction> search(searchReq req) {
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
        if (req.getMinDate() != null &&  req.getMaxDate() != null) {
            where.add("transaction_date BETWEEN :minDate AND :maxDate");
            parameters.addValue("minDate", req.getMinDate());
            parameters.addValue("maxDate", req.getMaxDate());
        }
        if (req.getDescription() != null) {
            where.add("description LIKE :description");
            parameters.addValue("description", "%" + req.getDescription() + "%");
        }

        sql += where.toString();
        return namedParameterJdbcTemplate.query(sql, parameters, new BeanPropertyRowMapper<>(Transaction.class));
    }
}
