package com.example.app_server;

public class UserInfo {

    private String user_id;
    private String pwd1;
    private String pwd2;
    private String email;
    private String nickname;

    public UserInfo(String user_id, String pwd1, String pwd2, String email, String nickname) {
        this.user_id = user_id;
        this.pwd1 = pwd1;
        this.pwd2 = pwd2;
        this.email = email;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPwd1() {
        return pwd1;
    }

    public void setPwd1(String pwd1) {
        this.pwd1 = pwd1;
    }

    public String getPwd2() {
        return pwd2;
    }

    public void setPwd2(String pwd2) {
        this.pwd2 = pwd2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
