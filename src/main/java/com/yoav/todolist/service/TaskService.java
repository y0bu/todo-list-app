package com.yoav.todolist.service;

import com.yoav.todolist.dao.ITaskDao;
import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class TaskService {

    private ITaskDao taskDao;

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
}
