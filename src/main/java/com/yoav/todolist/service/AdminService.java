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

    private final IAdminDao adminDao;

    @Autowired
    public AdminService(@Qualifier("adminRepository") IAdminDao adminDao) {
        this.adminDao = adminDao;
        if ( ! adminDao.IsBaseAdminAccountExist() ) {
            adminDao.add(new Admin("admin", "admin")); // if base admin account not exist
        }
    }

    /**
     * @return true if the account can be inserted and then in insert the account else it return false if
     * the adminName is already have been taken and then method return false and not inserting the account
     * */
    public boolean add(Admin admin) {
        if (isExistByAdminName(admin.getAdminName())) return false;
        adminDao.add(admin);
        return true;
    }

    public boolean isExistByAdminName(String adminName) {
        return adminDao.isExistByAdminName(adminName);
    }

    public boolean isExistByAdminNameAndPassword(String adminName, String password) {
        return adminDao.isExistByAdminNameAndPassword(adminName, password);
    }

}
