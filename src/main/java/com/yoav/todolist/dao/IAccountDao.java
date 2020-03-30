package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import org.springframework.stereotype.Component;

@Component
public interface IAccountDao {
    void delete(Account account);
    void add(Account account);
    boolean isExistByUsernameAndPassword(String username, String password);
    boolean isExistByUsername(String username);
    Account findByUsername(String username);
}
