package com.example.ayps;

import java.util.ArrayList;

public class User {

    private String userId;
    private String username;
    private String email;
    private String profileImg;
    private String provider;

    public User() {

    }

    public User( String userId, String username, String email, String profileImg, String provider ) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImg = profileImg;
        this.provider = provider;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}
