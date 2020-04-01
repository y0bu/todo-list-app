package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Admin;
import org.hibernate.query.Query;
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
        Query hqlQuery = getSession().createQuery("FROM Admin WHERE adminName = ?1");
        return hqlQuery.setString(1, adminName).list().size() == 1;
    }

    @Override
    public boolean isExistByAdminNameAndPassword(String adminName, String password) {
        Query hqlQuery = getSession().createQuery("FROM Admin WHERE adminName = ?1 AND password = ?2");
        hqlQuery.setString(1, adminName);
        hqlQuery.setString(2, password);
        return hqlQuery.list().size() == 1;
    }
}
