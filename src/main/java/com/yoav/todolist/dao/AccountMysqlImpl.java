package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountMysqlImpl extends AbstractHibernateDAO<Account> implements IAccountDao {

    public AccountMysqlImpl() {
        setOurClass(Account.class);
    }

    @Override
    public void add(Account account) {
        getSession().persist(account);
    }

    @Override
    public boolean isExistByUsernameAndPassword(String username, String password) {
        // TODO try to block SQL Injection
        List<Account> accounts = getSession().createQuery(
                "FROM Account WHERE" +
                " username = " +
                "'" + username + "'" +
                " AND password = " +
                "'" + password + "'")
                .list();
        return accounts.size() == 1;
    }

    @Override
    public boolean isExistByUsername(String username) {
        // TODO try to block SQL Injection
        List<Account> accounts = getSession().createQuery(
                "FROM Account WHERE" +
                   " username = " +
                   "'" + username + "'")
                   .list();
        return accounts.size() == 1;
    }

    @Override
    public Account findByUsername(String username) {
        List<Account> accounts = getSession().createQuery(
                "FROM Account WHERE" +
                " username = " +
                "'" + username + "'")
                .list();
        return accounts.get(0);
    }

    @Override
    public Account findById(int id) {
        return getById(id);
    }

    @Override
    public List<Account> findAll() {
        return getSession().createQuery("FROM Account").list();
    }
}
