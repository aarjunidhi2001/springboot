package com.example.mongodb.capstone.springboot.Service;

import com.example.mongodb.capstone.springboot.Models.User;
import com.example.mongodb.capstone.springboot.Repo.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
public class MailService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    JavaMailSender mailSender;


    public void register(User user) throws MessagingException, UnsupportedEncodingException {
        Random r=new Random();
        int n=r.nextInt();
        String code=Integer.toHexString(n);
        user.setVerifyotp(code);
        userRepo.save(user);
         sendVerificationMail(user,code);
    }
    @RequestMapping("/verify")
    public void sendVerificationMail(User user,String code) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmailId();
        String fromAddress = "aarjunidhi2001@gmail.com";
        String senderName = "capstone";
        String subject = "Please verify your mail for registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">click to verify</a></h3>"
                + "Thank You,<br>"
                + "Project capstone.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getFname()+" "+user.getLname());
        String verifyUrl = "http://127.0.0.1:8080/api/verify?code=" + code + "-" + user.getEmailId();
        content = content.replace("[[URL]]", verifyUrl);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public void forgetPassword(User user) throws MessagingException,UnsupportedEncodingException{
        Random r=new Random();
        int n=r.nextInt();
        String code=Integer.toHexString(n);
        user.setVerifyotp(code);
        System.out.println(code);
        userRepo.save(user);
        sendForgetMail(user,code);
    }
    @RequestMapping("/forget")
    public void sendForgetMail(User user,String code) throws MessagingException,UnsupportedEncodingException{
        String toAddress= user.getEmailId();
        String fromAddress="aarjunidhi2001@gmail.com";
        String senderName="capstone";
        String subject = "forget Password";
        String content = "Dear [[name]],<br>"
                + "please click below link to change your password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">click to verify</a></h3>"
                + "Thank You,<br>"
                + "Project capstone.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress,senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getFname()+" "+user.getLname());
        String verifyUrl = "http://127.0.0.1:8080/forgot?code="+code+"-"+user.getEmailId();
        content = content.replace("[[URL]]",verifyUrl);
        helper.setText(content,true);
        mailSender.send(message);
    }
    }

