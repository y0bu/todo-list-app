package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import org.hibernate.query.Query;
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
        Query hqlQuery = getSession().createQuery("FROM Account WHERE username = ?1 AND password = ?2");
        hqlQuery.setString(1, username);
        hqlQuery.setString(2, password);
        return hqlQuery.list().size() == 1;
    }

    @Override
    public boolean isExistByUsername(String username) {
        Query hqlQuery = getSession().createQuery("FROM Account WHERE username = ?1");
        return hqlQuery.setString(1, username).list().size() == 1;
    }

    @Override
    public Account findByUsername(String username) {
        Query hqlQuery = getSession().createQuery("FROM Account WHERE username = ?1");
        hqlQuery.setString(1, username);
        return (Account) hqlQuery.list().get(0);
    }

    @Override
    public List<Account> findAll() {
        return getAll();
    }
}
