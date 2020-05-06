package com.yoav.todolist.utils;

public class PasswordUtils {

    /**
     * the method take
     * @param password the password to check if it strong or not
     * and if the password is not strong enough the method
     * @return a note about the password like: "the password need to have at least two uppers character" but
     *                   if the password is strong the method
     * @return empty string
     * **/
    public static String chooseAlertForWeakPassword(String password) {
        if (password.length() < 8) {
            return "you need at least 8 character in the password";
        } else if (password.length() > 200) {
            return "the password is to long maximum length of the password is 200";
        }

        boolean hasSpecialChar = false;
        int uppers = 0;
        int digits = 0;
        int lowers = 0;
        for (int i = 0; i < password.length(); ++i) {
            char current = password.charAt(i);
            if (current >= 'a' && current <= 'z') lowers++;
            else if (current >= 'A' && current <= 'Z') uppers++;
            else if (current >= '0' && current <= '9') digits++;
            else hasSpecialChar = true;
        }
        if (!hasSpecialChar) return "the password must contains at least one special character";
        else if (lowers == 0) return "the password also need to contain at least one lower character";
        else if (uppers < 2) return "the password need to have at least two uppers character";
        else if (digits < 2) return "the password need to have at least two digits";
        else return "";
    }

}
