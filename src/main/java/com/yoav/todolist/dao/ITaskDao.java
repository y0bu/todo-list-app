package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * get all the tasks used in the api
     * @see com.yoav.todolist.controllers.api.TasksController
     * **/
    List<Task> getAll();

    /**
     * is for deleting all the tasks before updating because hibernate is shit and do not want to update my tasks
     * and now in clam mood: hibernate not updating correctly the tasks it just add tasks but not removing the unwanted
     * tasks
     * so we do the hard work and removing the task that hibernate don't want
     * @see TaskMysqlImpl
     * @see com.yoav.todolist.controllers.api.TasksController
     * **/
    void deleteAllByAccountId(int id);
}
