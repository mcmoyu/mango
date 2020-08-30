package com.moyu.mango.bmob;

import cn.bmob.v3.BmobObject;

public class MangoUser extends BmobObject{

    private String username;
    private String password;
//    private String phoneNumber;
//    private String email;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getEmail() {
//        return email;
//    }
}
