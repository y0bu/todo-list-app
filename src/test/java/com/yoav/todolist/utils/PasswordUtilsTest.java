package com.yoav.todolist.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordUtilsTest {

    @Test
    public void chooseAlertWeakPasswordTest_whenPasswordIsLessThen8Characters() {
        String password = "aaaa";
        assertThat(PasswordUtils.chooseAlertForWeakPassword(password)).
                isEqualTo("you need at least 8 character in the password");
    }

    @Test
    public void chooseAlertWeakPasswordTest_whenPasswordIsMoreThen200Characters() {
        String password = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        assertThat(PasswordUtils.chooseAlertForWeakPassword(password)).
                isEqualTo("the password is to long maximum length of the password is 200");
    }

    @Test
    public void chooseAlertWeakPasswordTest_whenThePasswordNotContainsSpecialCharacter() {
        String password = "1234567890";
        assertThat(PasswordUtils.chooseAlertForWeakPassword(password)).
                isEqualTo("the password must contains at least one special character");
    }

    @Test
    public void chooseAlertWeakPasswordTest_whenThePasswordNotContainsAtLeastOneLowerCharacter() {
        String password = "123456(*)7890";
        assertThat(PasswordUtils.chooseAlertForWeakPassword(password)).
                isEqualTo("the password also need to contain at least one lower character");
    }

    @Test
    public void chooseAlertWeakPasswordTest_whenThePasswordNotContainsAtLeastTwoUpperCaseCharacters() {
        String password = "a123456p(*)p7890a";
        assertThat(PasswordUtils.chooseAlertForWeakPassword(password)).
                isEqualTo("the password need to have at least two uppers character");
    }

    @Test
    public void chooseAlertWeakPasswordTest_whenThePasswordNotContainsAtLeastTwoDigits() {
        String password = "ap(*)PA1";
        assertThat(PasswordUtils.chooseAlertForWeakPassword(password)).
                isEqualTo("the password need to have at least two digits");
    }

    @Test
    public void chooseAlertWeakPasswordTest_whenThePasswordIsPerfect() {
        String password = "ap2(*)PA1";
        assertThat(PasswordUtils.chooseAlertForWeakPassword(password)).isEqualTo("");
    }

}