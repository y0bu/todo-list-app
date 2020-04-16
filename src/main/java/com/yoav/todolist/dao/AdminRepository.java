package com.yoav.todolist.dao;

import com.yoav.todolist.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>, IAdminDao {

    @Override
    default void add(Admin admin) {
        saveAndFlush(admin);
    }

    @Override
    default boolean isExistByAdminName(String adminName) {
        return isExistByAdminNameQuery(adminName).isPresent();
    }

    @Override
    default boolean isExistByAdminNameAndPassword(String adminName, String password) {
        return isExistByAdminNameAndPasswordQuery(adminName, password).isPresent();
    }

    @Query(value = "SELECT * FROM admins WHERE admins.admin_name = ?1", nativeQuery = true)
    @Transactional
    Optional<Admin> isExistByAdminNameQuery(String adminName);

    @Query(value = "SELECT * FROM admins WHERE admins.admin_name = ?1 AND admins.password = ?2", nativeQuery = true)
    @Transactional
    Optional<Admin> isExistByAdminNameAndPasswordQuery(String adminName, String password);


    @Transactional
    @Query(value = "SELECT * FROM admins WHERE admins.admin_name = 'admin' AND admins.password = 'admin'", nativeQuery = true)
    Optional<Admin> checkIfBaseAdminAccountExistQuery();

    @Override
    default boolean IsBaseAdminAccountExist() {
        return checkIfBaseAdminAccountExistQuery().isPresent();
    }
}
