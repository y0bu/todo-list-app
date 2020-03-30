package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.springframework.stereotype.Component;

@Component
public interface ITaskDao {
    void add(Task task, Account account);
    void deleteById(int id);
}
