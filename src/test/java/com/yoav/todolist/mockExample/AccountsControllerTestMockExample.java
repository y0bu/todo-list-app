package com.yoav.todolist.mockExample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import com.yoav.todolist.service.AccountService;
import com.yoav.todolist.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class AccountsControllerTestMockExample {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountServiceMock;

    @MockBean
    private TaskService taskServiceMock;

    private void setTheMocksSettings() {

        // Account Service Mock Settings

        when(accountServiceMock.add(new Account("yoav", "pass"))).thenReturn(true);
        when(accountServiceMock.add(new Account("exist", "pass"))).thenReturn(false);

        when(accountServiceMock.isExistByUsername(anyString())).thenReturn(false);
        when(accountServiceMock.isExistByUsername(eq("exist"))).thenReturn(true);

        when(accountServiceMock.findByUsername(anyString())).thenReturn(new Account("exist", "password"));
        when(accountServiceMock.findByUsername(eq("usernameNotExist"))).thenThrow(new NullPointerException("there is not existing account with this username therefor can find account by username"));

        Account account1 = new Account("username", "password");
        account1.setTasks(Arrays.asList(new Task("important task"), new Task("another task")));
        Account account2 = new Account("account", "secretKey");
        account2.setTasks(Arrays.asList(new Task("task1"), new Task("task2")));
        when(accountServiceMock.getAll()).thenReturn(Arrays.asList(account1, account2));

        when(accountServiceMock.isExistByUsernameAndPassword(isNull(), isNull())).thenReturn(false);
        when(accountServiceMock.isExistByUsernameAndPassword(anyString(), anyString())).thenReturn(true);

        // Task Service Mock Settings

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(account1.getTasks());
        tasks.addAll(account2.getTasks());
        when(taskServiceMock.getAll()).thenReturn(tasks);

        when(taskServiceMock.getById(anyInt())).thenReturn(new Task("task"));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllAccountsTest() throws Exception {
        setTheMocksSettings();
        mockMvc.perform(get("/api/account")).
                andExpect(status().isOk()).
                andExpect(content().json("" +
                        "[{'username':'username','password':'password'," +
                            "'tasks':" +
                                "[{'task':'important task'}," +
                                "{'task':'another task'}]}," +
                        "{'username':'account','password':'secretKey'," +
                            "tasks:" +
                                "[{'task':'task1'}," +
                                "{'task':'task2'}]}]"
                ));
    }

    @Test
    public void getCertainAccountTest_Found() throws Exception {
        setTheMocksSettings();
        mockMvc.
                perform(get("/api/account/notMatterWhatIPutHere")).
                andExpect(status().isOk()).
                andExpect(content().json("{'username':'exist','password':'password','tasks':[]}"));
    }

    @Test
    public void getCertainAccountTest_NotFound() throws Exception {
        setTheMocksSettings();
        mockMvc.
                perform(get("/api/account/usernameNotExist")).
                andExpect(mvcResult ->
                        assertThat(mvcResult.getResponse().getContentLength()).isEqualTo(0)); // expecting null
    }

    @Test
    public void addAccountTest_accountInsertedSuccessfully() throws Exception {
        setTheMocksSettings();
        mockMvc.
                perform(post("/api/account").
                        content(asJsonString(new Account("yoav", "pass"))).
                        contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());
    }

    @Test
    public void addAccountTest_usernameIsAlreadyHaveBeenTaken() throws Exception {
        setTheMocksSettings();
        mockMvc.
                perform(post("/api/account").
                        content(asJsonString(new Account("exist", "pass"))).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteAccountTest_deleteExistAccountDeletionShouldBeSuccessful() throws Exception {
        setTheMocksSettings();
        mockMvc.perform(delete("/api/account/yoav")).andExpect(status().isOk());
    }

    @Test
    public void deleteAccountTest_deleteNotExistingAccountDeletionShouldBeFailure() throws Exception {
        setTheMocksSettings();
        mockMvc.perform(delete("/api/account/usernameNotExist")).andExpect(status().isBadRequest());
    }

    @Test
    public void updateAccountTest_successUpdate() throws Exception {
        setTheMocksSettings();
        mockMvc.perform(put("/api/account/yoav").
                    content(asJsonString(new Account("yoavAbu", "paper"))).
                    contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk());
    }

    @Test
    public void updateAccount_failureUpdate() throws Exception {
        setTheMocksSettings();
        mockMvc.perform(put("/api/account/usernameNotExist").
                content(asJsonString(new Account("yoav", "best password"))).
                contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isBadRequest());
    }

}
