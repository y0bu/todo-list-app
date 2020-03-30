package com.yoav.todolist.service;

import com.yoav.todolist.dao.IAccountDao;
import com.yoav.todolist.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AccountService {

    private IAccountDao accountDao;

    @Autowired
    public AccountService(@Qualifier("accountMysqlImpl") IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public boolean isExistByUsernameAndPassword(String username, String password) {
        return accountDao.isExistByUsernameAndPassword(username, password);
    }

    public boolean isExistByUsername(String username) {
        return accountDao.isExistByUsername(username);
    }

    public void delete(Account account) {
        accountDao.delete(account);
    }

    public void add(Account account) {
        accountDao.add(account);
    }

    public Account findByUsername(String username) {
        return accountDao.findByUsername(username);
    }
}
