package com.yoav.todolist.controllers.api;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * this is a rest api controller
 * that offer operations(CRUD operations) to do on the account object(entity)
 * **/
@RestController
public class AccountsController {

    private final AccountService accountService;

    @Autowired // dependency injection of singleton pattern
    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * @return all the accounts information like username password tasks and id
     * **/
    @GetMapping("/api/account")
    public List<Account> getAllAccounts() {
        return accountService.getAll();
    }

    /**
     * @param username is for specifying which account you want to see
     * @return a specified account information
     * **/
    @GetMapping("/api/account/{username}")
    public Account getCertainAccount(@PathVariable String username) {
        try {
            return accountService.findByUsername(username);
        } catch (IndexOutOfBoundsException | NullPointerException e) { // if there is no existing username in the database
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param account that account we insert into the database
     *                but before that we checking whether username is already exist and that not allowed
     * @return "the username is already exist" if the username is already exist in the table which is not allowed
     * else if the username is not presenting in the database we can insert the account safely and return the message
     * "user is inserted to the database successfully"
     * **/
    @PostMapping("/api/account")
    public ResponseEntity<String> addAccount(@Valid @RequestBody Account account) {
        if (accountService.add(account)) return new ResponseEntity<>("user is inserted to the database successfully", new HttpHeaders(), HttpStatus.OK);
        return new ResponseEntity<>("the username is already exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * @param username this is the username that we going to delete
     * @return "the user deleted successfully" if everything is ok but if the username is not existing in the database
     * we returning "the user is not existing in the database" and we not deleting nothing
     * **/
    @DeleteMapping("/api/account/{username}")
    public ResponseEntity<String> deleteAccount(@PathVariable String username) {
        try {
            Account accountWantedToBeDeleted = accountService.findByUsername(username);
            accountService.delete(accountWantedToBeDeleted);
            return new ResponseEntity<String>("the user deleted successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return new ResponseEntity<String>("the user is not existing in the database", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @param account the new account that going to be switched with the old account
     * @param username is for knowing which account to update
     *
     * @return "user updated successfully" if everything is good
     * if there is no record with username {"username"} return "username do not exist"
     *
     * for understanding why i wort the line
     * taskService.deleteAllByAccountId(accountWantedToBeUpdated.getId());
     * go to the interface that declare that method
     * @see com.yoav.todolist.dao.ITaskDao
     * **/
    @PutMapping("/api/account/{username}")
    public ResponseEntity<String> updateAccount(@Valid @RequestBody Account account, @PathVariable String username) {
        try {
            Account accountWantedToBeUpdated = accountService.findByUsername(username);
            account.setId(accountWantedToBeUpdated.getId());
            accountService.update(account);
            return new ResponseEntity<String>("user updated successfully", new HttpHeaders(), HttpStatus.OK);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            return new ResponseEntity<String>("username do not exist", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }
}
