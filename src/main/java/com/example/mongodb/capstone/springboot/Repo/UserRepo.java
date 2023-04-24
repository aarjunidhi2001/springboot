package com.example.mongodb.capstone.springboot.Repo;

import com.example.mongodb.capstone.springboot.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User,String>{
}
