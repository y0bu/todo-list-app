package com.yoav.todolist.dao;

import com.yoav.todolist.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>, IAdminDao {

    @Override
    default void add(Admin admin) {
        saveAndFlush(admin);
    }

    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM admins WHERE admins.admin_name = ?1", nativeQuery = true)
    @Transactional
    @Override
    boolean isExistByAdminName(String adminName);

    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM admins WHERE admins.admin_name = ?1 AND admins.password = ?2", nativeQuery = true)
    @Transactional
    @Override
    boolean isExistByAdminNameAndPassword(String adminName, String password);

    @Transactional
    @Override
    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM admins WHERE admins.admin_name = 'admin' AND admins.password = 'admin'", nativeQuery = true)
    boolean IsBaseAdminAccountExist();

}
