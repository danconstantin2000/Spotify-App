package com.example.ApiGateway.controllers;

import com.example.ApiGateway.dtos.LoginDto;
import com.example.ApiGateway.services.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/gateway")
public class UserController {

    @Autowired
    UserService userService;
    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){

        String jwt_token=userService.Login(loginDto);
        if(jwt_token!=null){
            net.minidev.json.JSONObject jsonObject=new  net.minidev.json.JSONObject();
            jsonObject.put("jwt_token",jwt_token);
            JSONObject user=userService.authorize(jwt_token);
            String username = userService.getUserName(user.getInt("uid"));
            jsonObject.put("uid",user.get("uid"));
            JSONArray roles=user.getJSONArray("roles");
            jsonObject.put("roles",roles.toList());
            jsonObject.put("username",username);
            return new ResponseEntity<>(jsonObject,HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

}
