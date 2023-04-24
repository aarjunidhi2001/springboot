package com.example.mongodb.capstone.springboot.Models;

import lombok.Data;

@Data
public class LoginAuthReq {
    private String emailId;
    private String password;

}
