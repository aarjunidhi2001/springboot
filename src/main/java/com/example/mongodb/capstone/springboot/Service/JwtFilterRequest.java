package com.example.mongodb.capstone.springboot.Service;

import com.example.mongodb.capstone.springboot.Jwt.JwtToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Component
@Slf4j
public class JwtFilterRequest extends OncePerRequestFilter {
    @Autowired
    JwtToken jwtToken;
 
    @Autowired
    UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader=request.getHeader("Authorization");
        String username=null;
        String jwt=null;
        try{
            if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer")){
                jwt=authorizationHeader.substring(7);
                username=jwtToken.extractUsername(jwt);
            }
            if(username!=null && SecurityContextHolder.getContext().getAuthentication()!=null){
                UserDetails userDetails=this.userService.loadUserByUsername(username);
                if(jwtToken.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        catch(ExpiredJwtException e){
            Map<String,String> errors=new HashMap<>();
            response.setStatus(FORBIDDEN.value());
            errors.put("error","Jwt Token is expired,please log in again");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(),errors);
            return;
        }
        catch (SignatureException e){
            Map<String,String> errors=new HashMap<>();
            response.setStatus(FORBIDDEN.value());
            errors.put("error","JWT signature does not match locally computed signature");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(),errors);
            return;
        }
        catch (Exception e){
            Map<String,String> errors=new HashMap<>();
            response.setStatus(FORBIDDEN.value());
            errors.put("error","An error occured while Jwt Authentication");
            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(),errors);
            return;
        }
        filterChain.doFilter(request,response);
    }
}
