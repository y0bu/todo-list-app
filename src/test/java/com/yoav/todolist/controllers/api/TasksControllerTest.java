package com.yoav.todolist.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoav.todolist.dao.IAccountDao;
import com.yoav.todolist.dao.ITaskDao;
import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@Transactional
public class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("accountRepository")
    private IAccountDao accountDao;

    @Autowired
    @Qualifier("taskRepository")
    private ITaskDao taskDao;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Before
    public void deleteAllTheTableAndInsertSomeValues() {
        accountDao.deleteAllInBatch();
        taskDao.deleteAllInBatch();

        Account account1 = new Account("yoav", "abu");
        account1.addTask(new Task("spring"));
        account1.addTask(new Task("hibernate"));

        Account account2 = new Account("hello", "world");
        account2.addTask(new Task("java"));

        accountDao.add(account1);
        accountDao.add(account2);
    }

    @Test
    public void getAllTasksTest_thereIsNothingToCheckHereExceptTheSimpleScenario() throws Exception {
        mockMvc.
            perform(get("/api/task/")).
                andExpect(status().isOk()).
                andExpect(content().json("" + 
                    "[{'task':'spring'},{'task':'java'},{'task':'hibernate'}]"));
    }

    @Test
    public void getAllTasksBelongToSpecifiedUsernameTest_simpleScenarioWhereUserDoesExist() throws Exception {
        mockMvc.
            perform(get("/api/task/hello")).
                andExpect(status().isOk()).
                andExpect(content().json("" + 
                    "[{'task':'java'}]"));
    }

    @Test
    public void getAllTasksBelongToSpecifiedUsernameTest_simpleScenarioWhereUserIsNotExist() throws Exception {
        mockMvc.
            perform(get("/api/task/notExist")).
                andExpect(status().isOk()).
                andExpect(mvcResult -> {
                    assertThat(mvcResult.getResponse().getContentLength()).isEqualTo(0);
                });
    }

    @Test
    public void addTaskTest_simpleScenarioWhereUsernameExist_taskShouldBeInserted() throws Exception {
        mockMvc.
            perform(post("/api/task/hello").
            contentType(MediaType.APPLICATION_JSON).
            content(asJsonString(new Task("new task just got inserted")))).
                andExpect(status().isOk());
        
        // validating that the task have been added
        Optional<Account> account = accountDao.findByUsername("hello");
        assertThat(account).isPresent();
        
        List<Task> tasksOfAccount = account.get().getTasks();
        assertThat(tasksOfAccount).hasSize(2);
        
        int indexOfLastElementInTasksOfAccount = tasksOfAccount.size()-1;
        assertThat(tasksOfAccount.get(indexOfLastElementInTasksOfAccount).
            getTask()).isEqualTo("new task just got inserted");
    }

    @Test
    public void addTaskTest_simpleScenarioWhereUsernameDoNotExist_taskShouldNotBeInserted() throws Exception {
        mockMvc.
            perform(post("/api/task/notExist").
            contentType(MediaType.APPLICATION_JSON).
            content(asJsonString(new Task("the new task will not insert")))).
                andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTaskTest_simpleScenarioWhereUsernameExist_taskShouldBeDeleted() throws Exception {
        mockMvc.perform(delete("/api/task/yoav/2")).andExpect(status().isOk());

        Optional<Account> account = accountDao.findByUsername("yoav");
        assertThat(account).isPresent();
        List<Task> tasksOfAccount = account.get().getTasks();
        assertThat(tasksOfAccount).hasSize(1);
        assertThat(tasksOfAccount.get(0).getTask()).isEqualTo("spring");
    }

    @Test
    public void deleteTaskTest_simpleScenarioWhereUsernameExistButNumberOrderIsOutOfBounds() throws Exception {
        mockMvc.perform(delete("/api/task/yoav/20000000")).andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTaskTest_simpleScenarioWhereUsernameDoNotExist_taskWillNotBeDeleted() throws Exception {
        mockMvc.perform(delete("/api/task/notExist/1")).andExpect(status().isBadRequest());
    }

    @Test
    public void updateTasksTest_updatingNormallyWithUsernameThatIsExistAndValidListOfTasks() throws Exception {
        List<String> tasks = new ArrayList<>();
        tasks.add(asJsonString(new Task("exist1")));
        tasks.add(asJsonString(new Task("exist2")));

        mockMvc.perform(put("/api/task/hello").
            contentType(MediaType.APPLICATION_JSON).
            content(Arrays.toString(tasks.toArray()))).
                andExpect(status().isOk());

        // validating that the tasks updated

        Optional<Account> account = accountDao.findByUsername("hello");
        assertThat(account).isPresent();
        List<Task> tasksOfAccount = account.get().getTasks();
        assertThat(tasksOfAccount).hasSize(2);
        assertThat(tasksOfAccount.get(0).getTask()).isEqualTo("exist1");
        assertThat(tasksOfAccount.get(1).getTask()).isEqualTo("exist2");
    }

    @Test
    public void updateTasksTest_updatingWithUsernameThatNotExist_thereforeUpdateNotGonnaHappen() throws Exception {
        List<String> tasks = new ArrayList<>();
        tasks.add(asJsonString(new Task("NotExist1")));
        tasks.add(asJsonString(new Task("NotExist2")));

        mockMvc.perform(put("/api/task/notExist").
            contentType(MediaType.APPLICATION_JSON).
            content(Arrays.toString(tasks.toArray()))).
                andExpect(status().isBadRequest());
    }

}
