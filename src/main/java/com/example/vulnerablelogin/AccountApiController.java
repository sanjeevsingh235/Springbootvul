package com.example.vulnerablelogin;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountApiController {
    private final AccountRepository accountRepository;

    public AccountApiController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable("id") long id) {
        Account account = accountRepository.findById(id);
        return account == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(account);
    }

    @GetMapping("/count")
    public Map<String, Integer> countAccounts() {
        return Map.of("count", accountRepository.count());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addAccount(@RequestBody AccountRequest request) {
        accountRepository.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "created"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAccountById(@PathVariable("id") long id) {
        int deleted = accountRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteAccountByUsername(@RequestParam("username") String username) {
        int deleted = accountRepository.deleteByUsername(username);
        return ResponseEntity.ok(Map.of("deleted", deleted));
    }
}
