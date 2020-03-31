package com.yoav.todolist.dao;

import com.yoav.todolist.models.Admin;
import org.springframework.stereotype.Component;

@Component
public interface IAdminDao {
    void add(Admin admin);
    boolean isExistByAdminName(String adminName);
    boolean isExistByAdminNameAndPassword(String adminName, String password);
    void checkIfBaseAdminAccountExist();
}
