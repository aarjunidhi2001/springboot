package com.example.mongodb.capstone.springboot.Controller;

import com.example.mongodb.capstone.springboot.Jwt.JwtToken;
import com.example.mongodb.capstone.springboot.Models.LoginAuthReq;
import com.example.mongodb.capstone.springboot.Models.LoginAuthRes;
import com.example.mongodb.capstone.springboot.Models.User;
import com.example.mongodb.capstone.springboot.Repo.UserRepo;
import com.example.mongodb.capstone.springboot.Service.MailService;


import com.example.mongodb.capstone.springboot.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserAuthController {

    @Autowired
    UserRepo userRepo;
    @Autowired
    MailService mailService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;
    @Autowired
    JwtToken jwtToken;
    @Autowired
    PasswordEncoder passwordEncoder;


    @PostMapping("/signin")
    public String signin(@RequestBody User user) throws MessagingException, UnsupportedEncodingException {
        if (!userRepo.existsById(user.getEmailId())) {
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            String encryptpassword = bcrypt.encode(user.getPassword());
             user.setPassword(encryptpassword);
            user.setEnabled(false);
            user.setFriends(new ArrayList<>());
            user.setFriendrequest(new ArrayList<>());
            userRepo.save(user);
            mailService.register(user);
            return "Verification mail sent";
        } else {
            Optional<User> userDetails = userRepo.findById(user.getEmailId());
            if (userDetails.get().getEnabled()) {
                return "Already exists";
            } else {
                return "waiting for verification";
            }
        }

    }

    @GetMapping("/verify")
    ResponseEntity<?> verifyUser(@RequestParam String code) {
        String[] paramList = code.split("-");
        String verifyCode = paramList[0];
        String email = paramList[1];
        Optional<User> user = userRepo.findById(email);
        if (user.get().getVerifyotp().equals(verifyCode) && user.get().getVerifyotp() != null) {
            user.get().setEnabled(true);
            user.get().setVerifyotp(null);
            userRepo.save(user.get());
            return ResponseEntity.ok("Account verified");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Url");
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginAuthReq loginAuthReq) {
        String emailId = loginAuthReq.getEmailId();
      //  System.out.println(emailId);
        String encryptpassword = loginAuthReq.getPassword();
     //   System.out.println(encryptpassword);
        if (userRepo.existsById(emailId)) {
            Optional<User> user = userRepo.findById(emailId);
            if (user.get().getEnabled()) {
                try {
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailId, encryptpassword));
                } catch (Exception e) {
                 //   System.out.println(e);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mail or password");
                }
               // System.out.println("sss");
                final UserDetails userDetails = userService.loadUserByUsername(loginAuthReq.getEmailId());
                String jwt = jwtToken.generateToken(userDetails);
                return ResponseEntity.ok(jwt);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AuthenticationFailedException("Account not found"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Account not found ,please create account");
        }
    }

    @GetMapping("/forget/{email}")
    public ResponseEntity<?> forgetPwdLink(@PathVariable String email) throws  MessagingException,UnsupportedEncodingException{
        if(userRepo.existsById(email)){
            Optional<User> user=userRepo.findById(email);
            mailService.forgetPassword(user.get());
            return ResponseEntity.ok("forget password link has been sent to given mail");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email not found");
    }
    @PostMapping("/forget/verify")
    public ResponseEntity<?>forgetPwd(@RequestBody HashMap<String,String> forgetUser,@RequestParam String code){
        String[] paramList=code.split("-");
        String verifyCode=paramList[0];
        String emailId=paramList[1];
        try{
            Optional<User> user=userRepo.findById(emailId);
            if(user.get().getVerifyotp()!=null && user.get().getVerifyotp().equals(verifyCode)){
                if(forgetUser.get("newpassword").equals(forgetUser.get("Renewpassword"))){
                    user.get().setPassword(passwordEncoder.encode(forgetUser.get("newpassword")));
                    user.get().setVerifyotp(null);
                    userRepo.save(user.get());
                    return ResponseEntity.ok("password has been changed");
                }
                else{
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("confirm password not match");
                }
            } else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("verifiaction is not valid");
            }
        } catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification");
        }
    }

}
