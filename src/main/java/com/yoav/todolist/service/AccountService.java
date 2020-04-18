package com.yoav.todolist.service;

import com.yoav.todolist.dao.IAccountDao;
import com.yoav.todolist.dao.ITaskDao;
import com.yoav.todolist.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AccountService {

    private final IAccountDao accountDao;
    private final ITaskDao taskDao;

    @Autowired
    public AccountService(@Qualifier("accountRepository") IAccountDao accountDao, @Qualifier("taskRepository") ITaskDao taskDao) {
        this.accountDao = accountDao;
        this.taskDao = taskDao;
    }

    public boolean isExistByUsernameAndPassword(String username, String password) {
        return accountDao.isExistByUsernameAndPassword(username, password);
    }

    public boolean isExistByUsername(String username) {
        return accountDao.isExistByUsername(username);
    }

    public void deleteById(int id) {
        accountDao.deleteById(id);
    }

    public void add(Account account) {
        accountDao.add(account);
    }

    public Account findByUsername(String username) {
        return accountDao.findByUsername(username).orElseThrow(() -> new NullPointerException("there is not existing account with this username therefor can find account by username"));
    }

    public List<Account> getAll() {
        return accountDao.findAll();
    }

    public void delete(Account account) {
        accountDao.delete(account);
    }

    public Account update(Account account) {


        /*
         * this method deleteAllByAccountId is deleting all the tasks belong to the username that we specifying
         * we need this for updating tasks for that account the reason for deleting is hibernate/spring data jpa
         * is not deleting the rest of the unwanted tasks is leave them not deleted while we do not want them
         * so in here i am deleting all the tasks belong to specifying user for deleting unwanted tasks
         * */
        taskDao.deleteAllByAccountId(account.getId());


        return accountDao.update(account);
    }
}
