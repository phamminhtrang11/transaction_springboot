package com.example.transactionspringboot.Controller;

import com.example.transactionspringboot.Service.TransactionService;
import com.example.transactionspringboot.TranEntity.Transaction;
import com.example.transactionspringboot.TranEntity.searchReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TranController {

    private final TransactionService transactionService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public TranController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @ResponseBody
    public String getAllTransaction() {
        Map<String, Object> rs = new HashMap<>();

        List<Transaction> transactions = transactionService.getAllTransactions();

        rs.put("success", transactions);
        rs.put("redirectURL", "/success");

        try {
            String jsonResponse = mapper.writeValueAsString(rs);
            return jsonResponse;
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize response\"}";
        }
    }

    @PostMapping("/search")
    @ResponseBody
    public String search(@RequestBody searchReq req) {

        Map<String, Object> rs = new HashMap<>();

        List<Transaction> transactions = transactionService.search(req);

        rs.put("success", transactions);
        rs.put("redirectURL", "/search");

        try {
            String jsonResponse = mapper.writeValueAsString(rs);
            return jsonResponse;
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize response\"}";
        }
    }
}
