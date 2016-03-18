package com.hungdh.firebase;

/**
 * Created by hungdh on 14/03/2016.
 */
public class Util {
    public static  boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.length()>10;
    }

    public static  boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
