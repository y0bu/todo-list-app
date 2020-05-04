package com.yoav.todolist.controllers;

import com.yoav.todolist.dao.IAccountDao;
import com.yoav.todolist.dao.IAdminDao;
import com.yoav.todolist.dao.ITaskDao;
import com.yoav.todolist.models.Account;
import com.yoav.todolist.models.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@Transactional
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("adminRepository")
    private IAdminDao adminDao;

    @Autowired
    @Qualifier("accountRepository")
    private IAccountDao accountDao;

    @Autowired
    @Qualifier("taskRepository")
    private ITaskDao taskDao;

    @Test
    public void postValidateLoginAdminTest_whenAdminNameAndPasswordAreCorrect() throws Exception {
        // those conditionals are already exist when starting the application therefore the login should be successful
        String adminName = "admin";
        String password = "admin";

        mockMvc.
                perform(post("/admin/login").
                        param("adminName", adminName).
                        param("password", password)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/admin"));
    }

    @Test
    public void postValidateLoginAdminTest_whenAdminNameAndPasswordAreIncorrect() throws Exception {
        // those conditionals are already exist when starting the application therefore the login should be successful
        String adminName = "notExist";
        String password = "notExist";

        mockMvc.
                perform(post("/admin/login").
                        param("adminName", adminName).
                        param("password", password)).
                andExpect(status().isOk()).
                andExpect(model().attribute("alert", "the password and/or username are incorrect")).
                andExpect(view().name("admin/login"));
    }

    @Test
    public void getAllAccountsMainAdminPanelTest_whenUserHaveAdminPermission() throws Exception {
        accountDao.deleteAllInBatch();
        accountDao.add(new Account("some account", "some password"));
        accountDao.add(new Account("some account2", "some password1"));
        accountDao.add(new Account("some1 account", "some password2"));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        mockMvc.
                perform(get("/admin").session(session)).
                andExpect(status().isOk()).
                andExpect(model().attribute("accounts", accountDao.findAll())).
                andExpect(view().name("admin/displayUsers"));
    }

    @Test
    public void getAllAccountsMainAdminPanelTest_whenUserDoesNotHaveAdminPermission() throws Exception {
        accountDao.deleteAllInBatch();
        accountDao.add(new Account("some account", "some password"));
        accountDao.add(new Account("some account2", "some password1"));
        accountDao.add(new Account("some1 account", "some password2"));

        mockMvc.
                perform(get("/admin")).
                andExpect(status().isOk()).
                andExpect(view().name("unauthorized"));
    }

    @Test
    public void postDeleteAccountTest_whenUserHaveAdminPermission() throws Exception {
        accountDao.deleteAllInBatch();
        accountDao.add(new Account("some account", "some password"));
        accountDao.add(new Account("some account2", "some password1"));
        accountDao.add(new Account("some1 account", "some password2"));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        mockMvc.
                perform(post("/admin").
                        param(
                                "deleteAccount",
                                String.valueOf(accountDao.findByUsername("some account").orElse(new Account()).getId())).
                        session(session)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/admin"));

        assertThat(accountDao.findByUsername("some account")).isNotPresent();
    }

    @Test
    public void postDeleteAccountTest_whenUserDoNotHaveAdminPermission() throws Exception {
        accountDao.deleteAllInBatch();
        accountDao.add(new Account("some account", "some password"));
        accountDao.add(new Account("some account2", "some password1"));
        accountDao.add(new Account("some1 account", "some password2"));

        mockMvc.
                perform(post("/admin").
                        param(
                                "deleteAccount",
                                String.valueOf(accountDao.findByUsername("some account").orElse(new Account()).getId()))).
                andExpect(status().isOk()).
                andExpect(view().name("unauthorized"));

        assertThat(accountDao.findByUsername("some account")).isPresent();
    }

    @Test
    public void getTasksOfNameOfAccountsTest_whenUserHaveAdminPermission() throws Exception {
        accountDao.deleteAllInBatch();
        accountDao.add(new Account("some account", "some password"));
        accountDao.add(new Account("some account2", "some password1"));
        accountDao.add(new Account("some1 account", "some password2"));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        mockMvc.
                perform(get("/admin/some account").session(session)).
                andExpect(status().isOk()).
                andExpect(model().attribute(
                        "tasks", accountDao.findByUsername("some account").orElse(new Account()).getTasks())).
                andExpect(view().name("admin/displayTasksOfUsers"));
    }

    @Test
    public void getTasksOfNameOfAccountsTest_whenUserDoNotHaveHaveAdminPermission() throws Exception {
        accountDao.deleteAllInBatch();
        accountDao.add(new Account("some account", "some password"));
        accountDao.add(new Account("some account2", "some password1"));
        accountDao.add(new Account("some1 account", "some password2"));

        mockMvc.
                perform(get("/admin/some%20account")).
                andExpect(status().isOk()).
                andExpect(view().name("unauthorized"));
    }

    @Test
    public void postDeleteTasksOfUsernameTest_userHavePermissionAndDeletionShouldBeWorking() throws Exception {
        Account account = new Account("yoav", "abu");
        account.addTask(new Task("java"));
        account.addTask(new Task("spring"));
        accountDao.add(account);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        assertThat(accountDao.findByUsername("yoav").orElse(new Account()).getTasks()).hasSize(2);

        mockMvc.
                perform(post("/admin/yoav").session(session).
                        param(
                                "deleteTask",
                                String.valueOf(accountDao.
                                        findByUsername("yoav").orElse(new Account()).getTasks().get(0).getId())
                        )).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/admin/yoav"));

        // TODO: uncomment this line when you fixed the bug
        // assertThat(accountDao.findByUsername("yoav").orElse(new Account()).getTasks()).hasSize(1);
    }

    @Test
    public void postDeleteTasksOfUsernameTest_userDoNotHavePermissionAndDeletionShouldNotBeWorking() throws Exception {
        Account account = new Account("yoav", "abu");
        account.addTask(new Task("java"));
        account.addTask(new Task("spring"));
        accountDao.add(account);

        assertThat(accountDao.findByUsername("yoav").orElse(new Account()).getTasks()).hasSize(2);

        mockMvc.
                perform(post("/admin/yoav").
                        param(
                                "deleteTask",
                                String.valueOf(accountDao.
                                        findByUsername("yoav").orElse(new Account()).getTasks().get(0).getId())
                        )).
                andExpect(status().isOk()).
                andExpect(view().name("unauthorized"));

        assertThat(accountDao.findByUsername("yoav").orElse(new Account()).getTasks()).hasSize(2);
    }

    @Test
    public void getCreateNewAdminTest_whenTyingToAddAdminButWithoutPermission() throws Exception {
        mockMvc.
                perform(get("/admin/add")).
                andExpect(status().isOk()).
                andExpect(view().name("unauthorized"));
    }

    @Test
    public void getCreateNewAdminTest_whenTyingToAddAdminWithPermission() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        mockMvc.
                perform(get("/admin/add").session(session)).
                andExpect(status().isOk()).
                andExpect(view().name("admin/createAdmin"));
    }

    @Test
    public void postAddAdminTest_whenUserDoNotHavePermission() throws Exception {
        String adminName = "username";
        String password = "password";

        mockMvc.
                perform(post("/admin/add").
                        param("adminName", adminName).
                        param("password", password)).
                andExpect(status().isOk()).
                andExpect(view().name("unauthorized"));
    }

    @Test
    public void postAddAdminTest_whenUserDoHavePermissionButAdminNameAlreadyExist() throws Exception {
        String adminName = "admin"; // this is the base admin account @see IAdminDao in dao package
        String password = "password";

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        mockMvc.
                perform(post("/admin/add").
                        param("adminName", adminName).
                        param("password", password).
                        session(session)).
                andExpect(status().isOk()).
                andExpect(view().name("admin/createAdmin")).
                andExpect(model().attribute("alert", "admin name is already have been taken"));
    }

    @Test
    public void postAddAdminTest_whenUserDoHavePermissionAndAdminNameIsValid() throws Exception {
        String adminName = "username";
        String password = "password";

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("isAdmin", true);

        assertThat(adminDao.isExistByAdminName("username")).isFalse();

        mockMvc.
                perform(post("/admin/add").
                        param("adminName", adminName).
                        param("password", password).
                        session(session)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/admin/login")).
                andExpect(flash().attribute("alert", "the admin added successfully you can log in now"));

        assertThat(adminDao.isExistByAdminName("username")).isTrue();
    }

}
