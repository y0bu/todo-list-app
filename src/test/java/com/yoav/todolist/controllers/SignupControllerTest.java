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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@Transactional
public class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("accountRepository")
    private IAccountDao accountDao;

    @Test
    public void postSignupTest_whenUserTypedPasswordAndWhenUserAskedToTypePasswordAgainUserTypedThePasswordAgainWrong() throws Exception {
        String username = "yoav abu";
        String password = "pass";
        String passwordAgain = "password";

        mockMvc.
                perform(post("/signup").
                        param("username", username).
                        param("password", password).
                        param("passwordAgain", passwordAgain)).
                    andExpect(status().isOk()).
                    andExpect(view().name("signup/index")).
                    andExpect(model().attribute("alert", "password you typed is not the same as you typed above"));
    }

    @Test
    public void postSignupTest_WhenUsernameIsAlReadyHaveBeenTaken() throws Exception {
        Account account = new Account("yoav abu", "()poYU34x");
        account.addTask(new Task("hello"));
        account.addTask(new Task("this is second task"));
        account.addTask(new Task("third task"));
        accountDao.add(account);

        String username = "yoav abu";
        String password = "@#GH56fd";
        String passwordAgain = "@#GH56fd";

        mockMvc.
                perform(post("/signup").
                        param("username", username).
                        param("password", password).
                        param("passwordAgain", passwordAgain)).
                andExpect(status().isOk()).
                andExpect(view().name("signup/index")).
                andExpect(model().attribute("alert", "the username is already have been taken"));
    }

    @Test
    public void postSignupTest_WhenEverythingIsFine() throws Exception {
        String username = "user";
        String password = "@#GH56fd";
        String passwordAgain = "@#GH56fd";

        mockMvc.
                perform(post("/signup").
                        param("username", username).
                        param("password", password).
                        param("passwordAgain", passwordAgain)).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("/login")).
                andExpect(flash().attribute("alert", "you signed up successfully now you can log in"));
    }

}