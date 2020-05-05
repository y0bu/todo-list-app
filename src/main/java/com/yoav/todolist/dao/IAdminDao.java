package com.yoav.todolist.dao;

import com.yoav.todolist.models.Admin;
import org.springframework.stereotype.Component;

@Component
public interface IAdminDao {

    /**
     * this is used in
     * @see com.yoav.todolist.controllers.AdminController
     * for adding admin in the request path "/admin/add"
     * **/
    void add(Admin admin);

    /**
     * is for making sure that when admin want to create new admin we need to check that admin do not create new admin with
     * existing username
     * @see com.yoav.todolist.controllers.AdminController
     * **/
    boolean isExistByAdminName(String adminName);

    /**
     * is for making sure that the passed username and password are existing in the admins table in the database for login
     * @see com.yoav.todolist.controllers.AdminController
     * **/
    boolean isExistByAdminNameAndPassword(String adminName, String password);

    /**
     * is basically for making sure that the base admin account exist the base admin account is "admin" "admin"
     * the reason that we have base admin account is because user cant just register for new admin only admin can
     * create new admins so we need at least one admin in the starting of the application which is the base admin
     * @implNote if you want to use it for deployment change the base account to something more safe then "admin" "admin"
     * the base admin need to be exist in order to adding more admins because only admin can add admin so we need admin to
     * be existed
     * @see com.yoav.todolist.service.AdminService
     * to change the base admin account
     * @see AdminRepository
     * **/
    boolean IsBaseAdminAccountExist();

    /**
     * WARNING!!!!!!
     * @implNote USE THIS ONLY IN INTEGRATION TESTING
     * **/
    void deleteAllInBatch();
}
