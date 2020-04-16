package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface IAccountDao {

    /**
     * this method is for deleting account in the admin feature where admin can delete a accounts
     * @see com.yoav.todolist.controllers.AdminController
     * **/
    void deleteById(int id);

    /**
     * this is for adding account in the sign up page
     * @see com.yoav.todolist.controllers.SignupController
     * **/
    void add(Account account);

    /**
     * is for making sure that the passed username and password are existing in the accounts table in the database
     * @see com.yoav.todolist.controllers.LoginController
     * **/
    boolean isExistByUsernameAndPassword(String username, String password);

    /**
     * for making sure that user do not sign up with existing username in the table
     * @see com.yoav.todolist.controllers.SignupController
     * **/
    boolean isExistByUsername(String username);

    /**
     * one of this method usages is for fetching username form the session and from the cookie in the dashboard controller
     * for getting information like task of user and mush more see at
     * @see com.yoav.todolist.controllers.DashboardController
     * and for the path request /admin/{usernameOfRequestedAccount}
     * for getting all the account tasks
     * @see com.yoav.todolist.controllers.AdminController
     * **/
    Optional<Account> findByUsername(String username);

    /**
     * this is for getting all accounts in the admin main page
     * @see com.yoav.todolist.controllers.AdminController
     * **/
    List<Account> findAll();

    /**
     * is for deleting a account in the api feature
     * @see com.yoav.todolist.controllers.api.AccountsController
     * **/
    void delete(Account account);

    /**
     * this is for update account in the api feature
     * @see com.yoav.todolist.controllers.api.AccountsController
     * **/
    Account update(Account account);
}
