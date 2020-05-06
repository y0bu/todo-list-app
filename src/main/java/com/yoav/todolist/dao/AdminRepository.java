package com.yoav.todolist.dao;

import com.yoav.todolist.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>, IAdminDao {

    @Override
    default void add(Admin admin) {
        saveAndFlush(admin);
    }

    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM Admin WHERE adminName = ?1")
    @Override
    boolean isExistByAdminName(String adminName);

    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM Admin WHERE adminName = ?1 AND password = ?2")
    @Override
    boolean isExistByAdminNameAndPassword(String adminName, String password);

    @Override
    @Query(value = "SELECT CASE WHEN COUNT(*) = 1 THEN true ELSE false END FROM Admin WHERE adminName = 'admin' AND password = 'admin'")
    boolean IsBaseAdminAccountExist();

}
