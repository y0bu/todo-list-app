package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IAccountDao {
    void deleteById(int id);
    void add(Account account);
    boolean isExistByUsernameAndPassword(String username, String password);
    boolean isExistByUsername(String username);
    Account findByUsername(String username);
    Account findById(int id);
    List<Account> findAll();
}
