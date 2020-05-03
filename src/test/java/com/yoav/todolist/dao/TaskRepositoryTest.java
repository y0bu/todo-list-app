package com.yoav.todolist.dao;

import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@DataJpaTest
class TaskRepositoryTest {

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
    public void deleteAllByAccountIdTest_updatingAccountForDemonstrationTheUsageOfThisMethod() {
        // add account with list of tasks
        Account account = new Account("admin", "admin");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task1"));
        tasks.add(new Task("task2"));
        tasks.add(new Task("task3"));
        account.setTasks(tasks);
        accountDao.add(account);

        // asserting that all the tasks are inserted
        account = accountDao.findByUsername("admin").orElse(null);
        if (account == null) {
            fail();
        } else {
            assertThat(account.getTasks()).hasSize(3);
        }

        // update the account with new list of tasks
        List<Task> newTasks = new ArrayList<>();
        newTasks.add(new Task("java"));
        newTasks.add(new Task("hibernate spring boot"));

        // here we deleting all the account tasks before inserting new ones
        /*
        * this method deleteAllByAccountId is deleting all the tasks belong to the username that we specifying
        * we need this for updating tasks for that account the reason for deleting is hibernate/spring data jpa
        * is not deleting the rest of the unwanted tasks is leave them not deleted while we do not want them
        * so in here i am deleting all the tasks belong to specifying user for deleting unwanted tasks
        * */
        //taskDao.deleteAllByAccountId(account.getId());

        account.setTasks(newTasks);
        accountDao.add(account);

        // asserting that all the tasks are updated
        account = accountDao.findByUsername("admin").orElse(null);
        if (account == null) {
            fail();
        } else {
            assertThat(account.getTasks()).hasSize(2);
        }

        // new we want only to insert one task to admin account
        Task newTask = new Task("the new Task");
        taskDao.add(newTask, account);

        // asserting that the task is inserted
        account = accountDao.findByUsername("admin").orElse(null);
        if (account == null) {
            fail();
        } else {
            assertThat(account.getTasks()).hasSize(3);
        }
    }

}
