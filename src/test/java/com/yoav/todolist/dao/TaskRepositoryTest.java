package com.yoav.todolist.dao;

import com.yoav.todolist.exceptions.UsernameNotFoundException;
import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    @Qualifier("taskRepository")
    private ITaskDao taskDao;

    @Autowired
    @Qualifier("accountRepository")
    private IAccountDao accountDao;

    @BeforeEach
    public void deleteAllTheTableAndInsertSomeValues() {
        accountDao.deleteAllInBatch();
        taskDao.deleteAllInBatch();
    }

    @Test
    public void deleteByIdTest_simpleCaseWhereInsertingAccountWithTasksAndThenRemovingOneOfUserTask() {
        Account account = new Account("yoav", "abu");
        account.addTask(new Task("task1"));
        account.addTask(new Task("task2"));
        account.addTask(new Task("task3"));
        accountDao.add(account);

        assertThat(accountDao.findByUsername("yoav").orElseThrow(UsernameNotFoundException::new).getTasks()).hasSize(3);

        taskDao.deleteById(accountDao.findByUsername("yoav").orElseThrow(UsernameNotFoundException::new).getTasks().get(0).getId());

        entityManager.flush();
        entityManager.clear();

        assertThat(accountDao.findByUsername("yoav").orElseThrow(UsernameNotFoundException::new).getTasks()).hasSize(2);
    }

}
