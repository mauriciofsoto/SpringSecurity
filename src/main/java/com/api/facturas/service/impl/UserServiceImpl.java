package com.api.facturas.service.impl;

import com.api.facturas.constant.FacturaConstant;
import com.api.facturas.pojo.User;
import com.api.facturas.repository.UserRepository;
import com.api.facturas.security.CustomerDetailsService;
import com.api.facturas.security.jwt.JwtUtil;
import com.api.facturas.service.UserService;
import com.api.facturas.util.FacturaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    private CustomerDetailsService customerDetailsService;

    private JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> singUp(Map<String, String> requesMap) {
        log.info("Registro interno de un usuario{}", requesMap);
        try{
            if(validateSignUpMap(requesMap)){
                User user = userRepository.findByEmail(requesMap.get("email"));
                if(Objects.isNull(user)){
                    userRepository.save(getUserFromMap(requesMap));
                    return FacturaUtils.getResponseEntity("Usuario registrado con exito", HttpStatus.CREATED);
                }
                else{
                    return FacturaUtils.getResponseEntity("El usuario con ese mail ya existe", HttpStatus.BAD_REQUEST);
                }
            }
            else{
                return FacturaUtils.getResponseEntity(FacturaConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro del login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if(authentication.isAuthenticated()){
                if (customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+ jwtUtil.generateToken(
                            customerDetailsService.getUserDetail().getEmail(),
                            customerDetailsService.getUserDetail().getRole())
                            +"\"}", HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"mensaje\":\""+"Espera la aprobacion del administrador"+"\"}", HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception exception){
            log.error("{}", exception);
        }
        return new ResponseEntity<String>("{\"mensaje\":\""+"Credenciales incorrectas "+"\"}", HttpStatus.BAD_REQUEST);
    }

    private boolean validateSignUpMap(Map<String, String> requesMap){
        if(requesMap.containsKey("nombre")  && requesMap.containsKey("numeroDeContacto") && requesMap.containsKey("email") && requesMap.containsKey("password")){
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requesMap){
        User user = new User();
        user.setNombre(requesMap.get("nombre"));
        user.setNumeroDeContacto(requesMap.get("numeroDeContacto"));
        user.setEmail(requesMap.get("email"));
        user.setPassword(requesMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

}
