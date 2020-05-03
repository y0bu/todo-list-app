package com.yoav.todolist.controllers;

import com.yoav.todolist.dao.IAccountDao;
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

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@Transactional
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("accountRepository")
    private IAccountDao accountDao;

    @Test
    public void getDashboardTest_whenThereIsCookie() throws Exception {
        Account account = new Account("yoav", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task1"));
        tasks.add(new Task("task2"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "yoav");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(get("/dashboard").cookie(cookie)).
                andExpect(status().isOk()).
                andExpect(model().attribute(
                        "tasks", accountDao.findByUsername("yoav").orElse(new Account()).getTasks())).
                andExpect(view().name("dashboard/index"));
    }

    @Test
    public void getDashboardTest_whenThereIsSessionCookie() throws Exception {
        Account account = new Account("yoavAbu", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("taskJava"));
        tasks.add(new Task("taskSpring"));
        account.setTasks(tasks);
        accountDao.add(account);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("username", "yoavAbu");

        mockMvc.
                perform(get("/dashboard").session(session)).
                andExpect(status().isOk()).
                andExpect(model().attribute(
                        "tasks", accountDao.findByUsername("yoavAbu").orElse(new Account()).getTasks())).
                andExpect(view().name("dashboard/index"));
    }

    @Test
    public void getDashboardTest_whenThereIsNoSessionCookieAndNoCookie() throws Exception {
        mockMvc.
                perform(get("/dashboard")).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/"));
    }

    @Test
    public void postDashboardRemoveTaskTest_whenINotHaveCookieAndSessionCookieEither() throws Exception {
        mockMvc.
                perform(post("/dashboard").param("delete", "1")).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/"));
    }

    @Test
    public void postDashboardRemoveTaskTest_whenIHaveCookie() throws Exception {
        Account account = new Account("yoav-spring", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task12"));
        tasks.add(new Task("task21"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "yoav-spring");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(post("/dashboard").
                        cookie(cookie).
                        param(
                                "delete",
                                String.valueOf(
                                        accountDao.findByUsername("yoav-spring").orElse(new Account()).
                                        getTasks().get(0).getId()
                                ))).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void postDashboardRemoveTaskTest_whenIHaveCookieButITryToDeleteOtherTaskLikeHackerShit() throws Exception {
        Account account = new Account("yoav-hibernate", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task132"));
        tasks.add(new Task("task231"));
        account.setTasks(tasks);
        accountDao.add(account);

        account = new Account("some user", "spring-boot");
        tasks = new ArrayList<>();
        tasks.add(new Task("1f"));
        tasks.add(new Task("ff1"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "yoav-hibernate");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(post("/dashboard").
                        cookie(cookie).
                        param(
                                "delete",
                                String.valueOf(
                                        accountDao.findByUsername("some user").orElse(new Account()).
                                                getTasks().get(0).getId()
                                ))).
                andExpect(status().isOk()).
                andExpect(view().name("unauthorized"));
    }

    @Test
    public void postDashboardAddTaskTest_whenTheTaskIsEmpty1() throws Exception {
        Account account = new Account("some admin", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("your"));
        tasks.add(new Task("right"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "some admin");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(post("/dashboard").
                        cookie(cookie).
                        param(
                                "addTask",
                                " ")).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void postDashboardAddTaskTest_whenTheTaskIsEmpty2() throws Exception {
        Account account = new Account("some admin", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("your"));
        tasks.add(new Task("right"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "some admin");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(post("/dashboard").
                        cookie(cookie).
                        param(
                                "addTask",
                                "")).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void postDashboardAddTaskTest_whenTaskBiggerThen25Characters() throws Exception {
        Account account = new Account("some admin", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("your"));
        tasks.add(new Task("right"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "some admin");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(post("/dashboard").
                        cookie(cookie).
                        param(
                                "addTask",
                                "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void postDashboardAddTaskTest_whenIDoNotHaveCookieOrSessionCookieEther() throws Exception {
        mockMvc.
                perform(post("/dashboard").
                        param(
                                "addTask",
                                "this is a normal task")).
                andExpect(redirectedUrl("/"));
    }

    @Test
    public void postDashboardAddTaskTest_whenEverythingIsNormalAndInsertionShouldBeGood() throws Exception {
        Account account = new Account("some admin", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("your"));
        tasks.add(new Task("right"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "some admin");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where

        mockMvc.
                perform(post("/dashboard").
                        cookie(cookie).
                        param(
                                "addTask",
                                "this is a normal task")).
                andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    public void postDashboardLogoutTest_whenLogoutWithCookies() throws Exception {
        Account account = new Account("some admin", "spring-boot");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("your"));
        tasks.add(new Task("right"));
        account.setTasks(tasks);
        accountDao.add(account);

        Cookie cookie = new Cookie("username", "some admin");
        // cookie.setSecure(true); if the protocol HTTPS is used so uncomment this line
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days
        cookie.setPath("/"); // global cookie accessible every where


        mockMvc.
                perform(post("/dashboard").
                        cookie(cookie).
                        param("logout", (String) null)).
                andExpect(result ->
                        assertThat(
                                result.getResponse().getCookie("username").getValue()).isNull());
    }

}
