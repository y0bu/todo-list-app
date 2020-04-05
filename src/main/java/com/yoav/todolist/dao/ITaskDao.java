package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.springframework.stereotype.Component;

@Component
public interface ITaskDao {

    /**
     * is for simply adding task
     * @see com.yoav.todolist.controllers.DashboardController
     * **/
    void add(Task task, Account account);

    /**
     * is for deleting task in
     * @see com.yoav.todolist.controllers.DashboardController
     * **/
    void deleteById(int id);

    /**
     * when we deleting a task we need to make sure that the task wanted to be deleted is actually belong to the user
     * wants to delete the task so we need to **get** the task and then check if it belong to the user
     * want to delete the task (for security purposes)
     * @see com.yoav.todolist.controllers.DashboardController
     * **/
    Task getById(int id);
}
