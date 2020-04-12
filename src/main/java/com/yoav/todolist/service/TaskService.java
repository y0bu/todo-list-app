package com.yoav.todolist.service;

import com.yoav.todolist.dao.ITaskDao;
import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {

    private final ITaskDao taskDao;

    @Autowired
    public TaskService(@Qualifier("taskMysqlImpl") ITaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public void add(Task task, Account account) {
        taskDao.add(task, account);
    }

    public void delete(int id) {
        taskDao.deleteById(id);
    }

    public Task getById(int id) {
        return taskDao.getById(id);
    }

    public List<Task> getAll() {
        return taskDao.getAll();
    }

    public void deleteAllByAccountId(int id) {
        taskDao.deleteAllByAccountId(id);
    }

}
