package com.example.mongodb.capstone.springboot.Controller;

import com.example.mongodb.capstone.springboot.DTO.UserDto;
import com.example.mongodb.capstone.springboot.Models.User;
import com.example.mongodb.capstone.springboot.Repo.UserRepo;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.attoparser.prettyhtml.PrettyHtmlMarkupHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepo userRepo;

    @PutMapping("/update")
    public ResponseEntity<?> profileUpdate(@Validated @RequestBody UserDto userDto, BindingResult bindingResult) {
        //System.out.printf("Hello");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // System.out.println(email);
        if (userRepo.existsById(email)) {
            Optional<User> user = userRepo.findById(email);
            if (bindingResult.getAllErrors().isEmpty()) {
                user.get().setFname(userDto.getFname());
                userRepo.save(user.get());
                return ResponseEntity.ok("User has been updated");
            }
            List<ObjectError> errors = bindingResult.getAllErrors();
            List<String> listError = new ArrayList<>();
            for (ObjectError i : errors) {
                listError.add(i.getDefaultMessage());
            }
            HashMap<String, List> errorMessage = new HashMap<>();
            errorMessage.put("Validation error", listError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }

    @GetMapping("/friends")
    public ResponseEntity<?> friendsList() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepo.findById(email);
        if (!user.get().getFriends().isEmpty()) {
            return ResponseEntity.ok(user.get().getFriends());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("friend list is empty");

        }
    }

    @GetMapping("/request")
    public ResponseEntity<?> requestList() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepo.findById(email);
        if (!user.get().getFriendrequest().isEmpty()) {
            return ResponseEntity.ok(user.get().getFriendrequest());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("friends request is empty");
        }
    }

    @GetMapping("/follow/{id}")
    public ResponseEntity<?> follow(@PathVariable String id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepo.findById(email);
        Optional<User> thatuser = userRepo.findById(id);
        if (userRepo.existsById(id)) {
            if (user.get().getFriends().contains(id)) {
                if (thatuser.get().getFriendrequest().contains(email)) {
                    return ResponseEntity.ok("Already friends request sent");
                }
                return ResponseEntity.ok("you are already frieds with each other");
            }
            user.get().getFriends().add(id);
            thatuser.get().getFriendrequest().add(email);
            userRepo.save(user.get());
            userRepo.save(thatuser.get());
            return ResponseEntity.ok("friend request sent to given mail");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not found");
    }
    @GetMapping("/unfollow/{id}")
    public ResponseEntity<?> unfollow(@PathVariable String id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userRepo.existsById(email) && userRepo.existsById(id)){
            Optional<User>myuser=userRepo.findById(email);
            Optional<User> thatuser=userRepo.findById(id);
            if(myuser.get().getFriends().contains(id) && thatuser.get().getFriends().contains(email)){
                myuser.get().getFriends().remove(id);
                thatuser.get().getFriends().remove(email);
                userRepo.save(myuser.get());
                userRepo.save(thatuser.get());
                return ResponseEntity.ok("friend removed,next time sent request to be friends each other");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("friend not in list");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");

        }
    }
    @GetMapping("/accept/{id}")
    public ResponseEntity<?> acceptReq(@PathVariable String id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        if(userRepo.existsById(email) && userRepo.existsById(id)){
            Optional<User> myuser=userRepo.findById(email);
            Optional<User> thatuser=userRepo.findById(id);
            if(myuser.get().getFriendrequest().contains(id)){
                myuser.get().getFriends().add(id);
                ArrayList<String> myUserReq=myuser.get().getFriendrequest();
                myUserReq.remove(id);
                thatuser.get().getFriends().add(email);
                userRepo.save(thatuser.get());
                userRepo.save(myuser.get());
                return ResponseEntity.ok("friend request accepted");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("friend req not found");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
    }
    @GetMapping("/rejectReq/{id}")
    public ResponseEntity<?>reject(@PathVariable String id){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user=userRepo.findById(email);
        if(user.isPresent()){
            user.get().getFriendrequest().remove(id);
            userRepo.save(user.get());
            return ResponseEntity.ok("friend request is rejected");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No Request");
    }

}
