package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.springframework.stereotype.Repository;

@Repository
public class TaskMysqlImpl extends AbstractHibernateDAO<Task> implements ITaskDao {

    public TaskMysqlImpl() {
        setOurClass(Task.class);
    }

    @Override
    public void add(Task task, Account account) {
        task.setAccount(account);
        update(task);
    }

    @Override
    public void deleteAllByAccountId(int id) {
        getSession().createQuery("DELETE FROM Task WHERE account = " + id).executeUpdate();
    }

    @Override
    public void deleteById(int id) {
        getSession().createQuery("DELETE FROM Task WHERE id = " + id).executeUpdate();
    }
}
