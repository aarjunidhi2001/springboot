package com.example.mongodb.capstone.springboot.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@Document(collection="Users")
public class User {
    @Id
    private String emailId;
    private String fname;
    private String lname;
    private String password;
    private String dob;

    private String provider="LOCAL";
    private String role="USER";
    private Boolean enabled = false;
    private String verifyotp;
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> friendrequest = new ArrayList<>();

    public User(String emailId, String fname, String lname, String password, String dob, String provider, String role, Boolean enabled, String verifyotp, ArrayList<String> friends, ArrayList<String> friendrequest) {
        this.emailId = emailId;
        this.fname = fname;
        this.lname = lname;
        this.password = password;
        this.dob = dob;
        this.provider = provider;
        this.role = role;
        this.enabled = enabled;
        this.verifyotp = verifyotp;
        this.friends = friends;
        this.friendrequest = friendrequest;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerifyotp() {
        return verifyotp;
    }

    public void setVerifyotp(String verifyotp) {
        this.verifyotp = verifyotp;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getFriendrequest() {
        return friendrequest;
    }

    public void setFriendrequest(ArrayList<String> friendrequest) {
        this.friendrequest = friendrequest;
    }
}
