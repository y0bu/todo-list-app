package com.yoav.todolist.controllers.api;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AccountsController {

    private final AccountService accountService;
    private final TaskService taskService;

    @Autowired
    public AccountsController(AccountService accountService, TaskService taskService) {
        this.taskService = taskService;
        this.accountService = accountService;
    }

    @GetMapping("/api/account")
    public List<Account> getAllAccounts() {
        return accountService.getAll();
    }

    @GetMapping("/api/account/{username}")
    public Account getCertainAccount(@PathVariable String username) {
        try {
            return accountService.findByUsername(username);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/api/account")
    public ResponseEntity addAccount(@Valid @RequestBody Account account) {
        if (accountService.isExistByUsername(account.getUsername())) {
            return new ResponseEntity("the username is already exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        accountService.add(account);
        return new ResponseEntity("user is inserted to the database successfully", new HttpHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/api/account/{username}")
    public ResponseEntity deleteAccount(@PathVariable String username) {
        try {
            Account accountWantedToBeDeleted = accountService.findByUsername(username);
            accountService.delete(accountWantedToBeDeleted);
            return new ResponseEntity("the user deleted successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException e) {
            return new ResponseEntity("the user is not existing in the database", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/api/account/{username}")
    public ResponseEntity updateAccount(@Valid @RequestBody Account account, @PathVariable String username) {
        try {
            Account accountWantedToBeUpdated = accountService.findByUsername(username);
            account.setId(accountWantedToBeUpdated.getId());
            taskService.deleteAllByAccountId(accountWantedToBeUpdated.getId());
            accountService.update(account);
            return new ResponseEntity("user updated successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return new ResponseEntity("username do not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }
}
