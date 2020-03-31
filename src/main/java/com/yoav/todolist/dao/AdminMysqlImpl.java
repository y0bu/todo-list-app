package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Admin;
import org.springframework.stereotype.Repository;

@Repository
public class AdminMysqlImpl extends AbstractHibernateDAO<Admin> implements IAdminDao {

    public AdminMysqlImpl() {
        setOurClass(Admin.class);
    }

    public void checkIfBaseAdminAccountExist() {
        if (!(getSession().createQuery("FROM Admin WHERE adminName = 'admin'").list().size() == 1)) {
            save(new Admin("admin", "admin"));
        }
    }

    @Override
    public void add(Admin admin) {
        save(admin);
    }

    @Override
    public boolean isExistByAdminName(String adminName) {
        // TODO try to prevent sql injection
        return getSession().createQuery("FROM Admin WHERE adminName = '" + adminName + "'").list().size() == 1;
    }

    @Override
    public boolean isExistByAdminNameAndPassword(String adminName, String password) {
        // TODO try to prevent sql injection
        return getSession().createQuery("FROM Admin WHERE adminName = '" + adminName + "' AND password = '" + password + "'")
                .list()
                .size() == 1;
    }
}
