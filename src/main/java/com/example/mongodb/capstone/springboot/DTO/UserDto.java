package com.example.mongodb.capstone.springboot.DTO;

import java.util.ArrayList;

public class UserDto {
    public String fname;
    public ArrayList<String>List=new ArrayList<>();
    public ArrayList<String>getList(){
        return List;
    }
    public void setList(ArrayList<String>list){
        List=list;
    }
    public String getFname(){
        return fname;
    }
    public void setFname(String fname){
        this.fname=fname;
    }

}
