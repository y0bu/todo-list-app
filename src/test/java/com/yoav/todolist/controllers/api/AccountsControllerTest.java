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
public class AccountsControllerTest {

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
    public void getAllAccountsTest() throws Exception {
        mockMvc.
                perform(get("/api/account")).
                andExpect(status().isOk()).
                andExpect(content().json("" +
                        "[{'username':'yoav','password':'abu',tasks:" +
                            "[{'task':'spring'},{'task':'hibernate'}]}," +
                        "{'username':'hello','password':'world','tasks':" +
                            "[{'task':'java'}]}]"));
    }

    @Test
    public void getCertainAccountTest_getAccountThatExist() throws Exception {
        mockMvc.
                perform(get("/api/account/yoav")).
                andExpect(status().isOk()).
                andExpect(content().json("" +
                        "{'username':'yoav','password':'abu'," +
                        "'tasks':" +
                            "[{'task':'spring'},{'task':'hibernate'}]}"));
    }

    @Test
    public void getCertainAccountTest_getAccountThatDoesNotExist_badRequest() throws Exception {
        mockMvc.
                perform(get("/api/account/justUserThatNotExist")).
                andExpect(mvcResult ->
                        assertThat(mvcResult.getResponse().getContentLength()).isEqualTo(0)); // expecting null
    }

    @Test
    public void addAccountTest_addingAccountBasicScenario() throws Exception {
        Account newAccount = new Account("another yoav", "pass");
        newAccount.addTask(new Task("task1"));
        newAccount.addTask(new Task("task2"));
        mockMvc.
                perform(post("/api/account").
                        content(asJsonString(newAccount)).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());

        // validating that account really inserted
        Optional<Account> theInsertedAccount = accountDao.findByUsername("another yoav");
        assertThat(theInsertedAccount).isPresent();
        assertThat(theInsertedAccount.get().getTasks()).hasSize(2);
        assertThat(theInsertedAccount.get().getPassword()).isEqualTo("pass");
    }

    @Test
    public void addAccountTest_addingAccountWithUsernameAlreadyExist() throws Exception {
        Account newAccount = new Account("yoav", "pass");
        newAccount.addTask(new Task("task1"));
        newAccount.addTask(new Task("task2"));
        mockMvc.
                perform(post("/api/account").
                        content(asJsonString(newAccount)).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    }

    @Test
    public void deleteAccountTest_deleteWithUsernameThatAlreadyExist_deletionSuccess() throws Exception {
        mockMvc.perform(delete("/api/account/yoav")).andExpect(status().isOk());
        // making sure that the account with username "yoav" is actually deleted
        Optional<Account> deletedAccount = accountDao.findByUsername("yoav");
        assertThat(deletedAccount).isNotPresent();
    }

    @Test
    public void deleteAccountTest_deleteWithUsernameThatNotExist_deletionFailure() throws Exception {
        mockMvc.perform(delete("/api/account/thisUsernameDoNotExist")).andExpect(status().isBadRequest());
    }

    @Test
    public void updateAccountTest_updateWithUsernameThatAlreadyExist_updateSuccessful() throws Exception {
        Account newAccount = new Account("yoav", "other password");
        newAccount.addTask(new Task("this is a new task"));
        newAccount.addTask(new Task("and this is a new task number 2"));
        newAccount.addTask(new Task("and this is a new task number 3"));
        newAccount.addTask(new Task("and this is a new task number 4"));
        mockMvc.perform(put("/api/account/yoav").
                content(asJsonString(newAccount)).
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());

        // validate that account with username "yoav" have been updated
        Optional<Account> updatedAccount = accountDao.findByUsername("yoav");
        assertThat(updatedAccount).isPresent();
        assertThat(updatedAccount.get().getPassword()).isEqualTo("other password");
        assertThat(updatedAccount.get().getTasks()).hasSize(4);
    }

    @Test
    public void updateAccountTest_updateWithUsernameThatAlreadyExistAlsoChangeTheUsernameThisTest_updateSuccessful() throws Exception {
        Account newAccount = new Account("programmer", "lots of passwords");
        newAccount.addTask(new Task("this is a new task"));
        mockMvc.perform(put("/api/account/yoav").
                content(asJsonString(newAccount)).
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());

        // validate that account with username "yoav"(that change to "programmer") have been updated
        Optional<Account> updatedAccount = accountDao.findByUsername("programmer");
        assertThat(updatedAccount).isPresent();
        assertThat(updatedAccount.get().getPassword()).isEqualTo("lots of passwords");
        assertThat(updatedAccount.get().getTasks()).hasSize(1);
    }

    @Test
    public void updateAccountTest_updateWithUsernameThatNotExistTest_updateFailure() throws Exception {
        Account newAccount = new Account("programmer", "lots of passwords");
        newAccount.addTask(new Task("this is a def"));
        newAccount.addTask(new Task("this is afdsfsdfewr"));
        mockMvc.perform(put("/api/account/fdsfdsfds").
                content(asJsonString(newAccount)).
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    }

}
