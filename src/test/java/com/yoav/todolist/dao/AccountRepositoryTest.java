package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
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
class AccountRepositoryTest {

    @Autowired
    @Qualifier("accountRepository")
    private IAccountDao accountDao;

    @BeforeEach
    public void deleteAllTheTableAndInsertSomeValues() {
        accountDao.deleteAllInBatch();
        accountDao.add(new Account("yoav", "abu"));
        accountDao.add(new Account("star", "abu"));
        accountDao.add(new Account("Us3r", "passWar"));
        accountDao.add(new Account("xyz", "passxyzword"));
    }

    // Basic Scenarios Tests

    @Test
    public void isExistByUsernameTest_shouldNotFindBasicScenario() {
        assertThat(accountDao.isExistByUsername("notGoingToFindIt")).isFalse();
    }

    @Test
    public void isExistByUsernameTest_shouldFindBasicScenario() {
        accountDao.add(new Account("insertedShouldFindMe", "pass"));
        assertThat(accountDao.isExistByUsername("insertedShouldFindMe")).isTrue();
    }

    @Test
    public void isExistByUsernameAndPasswordTest_shouldNotFindBasicScenario() {
        assertThat(accountDao.isExistByUsernameAndPassword("notExist", "notExist")).isFalse();
        assertThat(accountDao.isExistByUsernameAndPassword("notExist", "alsoNotExist")).isFalse();
        assertThat(accountDao.isExistByUsernameAndPassword("alsoNotExist", "notExist")).isFalse();
    }

    @Test
    public void isExistByUsernameAndPasswordTest_shouldFindBasicScenario() {
        accountDao.add(new Account("exist", "exist"));
        accountDao.add(new Account("exist", "alsoExist"));
        accountDao.add(new Account("alsoExist", "exist"));

        assertThat(accountDao.isExistByUsernameAndPassword("exist", "exist")).isTrue();
        assertThat(accountDao.isExistByUsernameAndPassword("alsoExist", "exist")).isTrue();
        assertThat(accountDao.isExistByUsernameAndPassword("exist", "alsoExist")).isTrue();
    }

    @Test
    public void findByUsernameTest_shouldFindBasicScenario() {
        Account account = new Account("admin", "strongPassword");
        accountDao.add(account);

        assertThat(accountDao.findByUsername(account.getUsername())).isPresent();
    }

    @Test
    public void findByUsernameTest_shouldNotFindBasicScenario() {
        Account account = new Account("admin", "strongPassword");
        accountDao.add(account);

        assertThat(accountDao.findByUsername("IDontThingItWillFindThatUser")).isNotPresent();
        assertThat(accountDao.findByUsername(account.getPassword())).isNotPresent();
    }

    // SQL Injection Prevention Tests

    @Test
    public void findByUsernameTest_SQLInjectionPrevention() {
        accountDao.add(new Account("admin", "admin"));

        assertThat(accountDao.findByUsername("'")).isNotPresent();
        assertThat(accountDao.findByUsername(" ' OR '1' = '1'")).isNotPresent();
    }

    @Test
    public void isExistByUsernameAndPasswordTest_SQLInjectionPrevention() {
        accountDao.add(new Account("admin", "admin"));

        assertThat(accountDao.isExistByUsernameAndPassword("'", "admin")).isFalse();
        assertThat(accountDao.isExistByUsernameAndPassword("admin", "'")).isFalse();
        assertThat(accountDao.isExistByUsernameAndPassword(" ' OR '", " != 'something' --")).isFalse();
        assertThat(accountDao.isExistByUsernameAndPassword("' OR '1' = '1'", " --")).isFalse();
    }

    @Test
    public void isExistByUsernameTest_SQLInjectionPrevention() {
        accountDao.add(new Account("admin", "admin"));

        assertThat(accountDao.isExistByUsername("'")).isFalse();
        assertThat(accountDao.isExistByUsername(" ' OR '1' = '1' --")).isFalse();
    }

}