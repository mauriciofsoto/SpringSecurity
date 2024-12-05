package com.api.facturas.controller;

import com.api.facturas.constant.FacturaConstant;
import com.api.facturas.service.UserService;
import com.api.facturas.util.FacturaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> registrarUsuario(@RequestBody(required = true)Map<String, String> requestMap){
        try{
            return userService.singUp(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true)  Map<String, String> requestMap){
        try {
            return userService.login(requestMap);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
