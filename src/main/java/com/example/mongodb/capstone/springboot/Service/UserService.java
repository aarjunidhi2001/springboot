package com.example.mongodb.capstone.springboot.Service;

import com.example.mongodb.capstone.springboot.Models.User;
import com.example.mongodb.capstone.springboot.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"UserLogin"})
public class UserService implements UserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Override
    @Cacheable(key="#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Optional<User>foundUser=userRepo.findById(email);
        if(foundUser.isEmpty()){
            return null;
        }
        String emailId=foundUser.get().getEmailId();
        String password=foundUser.get().getPassword();
        return new org.springframework.security.core.userdetails.User(emailId,password,new ArrayList<>());
    }
}
