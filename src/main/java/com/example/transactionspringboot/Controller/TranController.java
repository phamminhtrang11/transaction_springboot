package com.example.transactionspringboot.Controller;

import com.example.transactionspringboot.Service.TransactionService;
import com.example.transactionspringboot.TranEntity.Transaction;
import com.example.transactionspringboot.TranEntity.searchReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public String getAllTransaction(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> rs = new HashMap<>();

        List<Transaction> transactions = transactionService.getAllTransactions(page, size);

        rs.put("success", transactions);
        rs.put("redirectURL", "/success");

        try {
            String jsonResponse = mapper.writeValueAsString(rs);
            return jsonResponse;
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Failed to serialize response\"}";
        }
    }
    @PostMapping
    public ResponseEntity<String> addTransaction(@RequestBody Transaction transaction) {
        try {
            int addedTransaction = transactionService.addTransaction(transaction);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", addedTransaction);
            responseMap.put("redirectURL", "/success");
            String jsonResponse = mapper.writeValueAsString(responseMap);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Failed to serialize response\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable("id") Long id, @RequestBody Transaction transaction) {
        try {
            int updatedTransaction = transactionService.updateTransaction(id, transaction);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", updatedTransaction);
            responseMap.put("redirectURL", "/success");
            String jsonResponse = mapper.writeValueAsString(responseMap);
            return ResponseEntity.ok(jsonResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Failed to serialize response\"}");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable("id") Long id) {
        try {
            transactionService.deleteTransaction(id);
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", "Transaction deleted successfully");
            responseMap.put("redirectURL", "/success");

            String jsonResponse = mapper.writeValueAsString(responseMap);
            return ResponseEntity.ok(jsonResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Failed to serialize response\"}");
        }
    }


    @PostMapping("/search")
    public ResponseEntity<String> search(@RequestBody searchReq req, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> responseMap = new HashMap<>();
        List<Transaction> transactions = transactionService.search(req, page, size);

        responseMap.put("success", transactions);
        responseMap.put("redirectURL", "/search");

        try {
            String jsonResponse = mapper.writeValueAsString(responseMap);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Failed to serialize response\"}");
        }
    }

}