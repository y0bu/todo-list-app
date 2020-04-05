package com.yoav.todolist.service;

import com.yoav.todolist.dao.IAdminDao;
import com.yoav.todolist.models.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AdminService {

    private IAdminDao adminDao;

    @Autowired
    public AdminService(@Qualifier("adminMysqlImpl") IAdminDao adminDao) {
        this.adminDao = adminDao;
    }

    public void checkIfBaseAdminAccountExist() {
        adminDao.checkIfBaseAdminAccountExist();
    }

    public void add(Admin admin) {
        adminDao.add(admin);
    }

    public boolean isExistByAdminName(String adminName) {
        return adminDao.isExistByAdminName(adminName);
    }

    public boolean isExistByAdminNameAndPassword(String adminName, String password) {
        return adminDao.isExistByAdminNameAndPassword(adminName, password);
    }
}
