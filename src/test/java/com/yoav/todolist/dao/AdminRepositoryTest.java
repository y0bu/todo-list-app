package com.yoav.todolist.dao;

import com.yoav.todolist.models.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    @Qualifier("adminRepository")
    private IAdminDao adminDao;

    @BeforeEach
    public void deleteAllRecordAfterEach() {
        adminDao.deleteAllInBatch();
        adminDao.add(new Admin("yoav", "abu"));
        adminDao.add(new Admin("star", "abu"));
        adminDao.add(new Admin("Us3r", "passWar"));
        adminDao.add(new Admin("xyz", "passxyzword"));
    }

    // Basic Scenarios Tests

    @Test
    public void IsBaseAdminAccountExistTest_thereIsNoBaseAdminAccountBasicScenario() {
        assertThat(adminDao.IsBaseAdminAccountExist()).isFalse();
    }

    @Test
    public void IsBaseAdminAccountExistTest_thereIsBaseAdminAccountBasicScenario() {
        adminDao.add(new Admin("admin", "admin")); // base admin you can change it whenever you want
        assertThat(adminDao.IsBaseAdminAccountExist()).isTrue();
    }

    @Test
    public void isExistByAdminNameTest_notExistAdminNameBasicScenario() {
        assertThat(adminDao.isExistByAdminName("uuuuuuuuuuu")).isFalse();
        assertThat(adminDao.isExistByAdminName("admi")).isFalse();
    }

    @Test
    public void isExistByAdminNameTest_adminNameExistBasicScenario() {
        assertThat(adminDao.isExistByAdminName("yoav")).isTrue();
        assertThat(adminDao.isExistByAdminName("star")).isTrue();
    }

    @Test
    public void isExistByAdminNameAndPasswordTest_failureBasicScenario() {
        assertThat(adminDao.isExistByAdminNameAndPassword("yoav", "ab")).isFalse();
        assertThat(adminDao.isExistByAdminNameAndPassword("yoa", "abu")).isFalse();
        assertThat(adminDao.isExistByAdminNameAndPassword("dasdas", "dsa")).isFalse();
        assertThat(adminDao.isExistByAdminNameAndPassword("abu", "yoav")).isFalse();
    }

    @Test
    public void isExistByAdminNameAndPasswordTest_successBasicScenario() {
        adminDao.add(new Admin("admin", "admin"));
        assertThat(adminDao.isExistByAdminNameAndPassword("yoav", "abu")).isTrue();
        assertThat(adminDao.isExistByAdminNameAndPassword("admin", "admin")).isTrue();
        assertThat(adminDao.isExistByAdminNameAndPassword("star", "abu")).isTrue();
        assertThat(adminDao.isExistByAdminNameAndPassword("Us3r", "passWar")).isTrue();
    }

    // SQL Injection Prevention Tests

    @Test
    public void isExistByAdminNameTest_SQLInjectionPrevention() {
        assertThat(adminDao.isExistByAdminName("'")).isFalse();
        assertThat(adminDao.isExistByAdminName(" ' OR '1' = '1'")).isFalse();
    }

    // SELECT * FROM admins WHERE admins.admin_name = ?1 AND admins.password = ?2
    @Test
    public void isExistByAdminNameAndPasswordTest_SQLInjectionPrevention() {
        assertThat(adminDao.isExistByAdminNameAndPassword("'", "'")).isFalse();
        assertThat(adminDao.isExistByAdminNameAndPassword(" ' OR '", " != '1' --")).isFalse();
    }

}