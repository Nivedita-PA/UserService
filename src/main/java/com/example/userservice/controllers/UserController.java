package com.example.userservice.controllers;

import com.example.userservice.dtos.*;
import com.example.userservice.dtos.ResponseStatus;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        try{
            Token token = userService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
            loginResponseDTO.setToken(token.getValue());
            loginResponseDTO.setResponseStatus(ResponseStatus.SUCCESS);
        }catch (Exception e){
            loginResponseDTO.setResponseStatus(ResponseStatus.FAILURE);
        }
          return loginResponseDTO;
    }

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody SignUpRequestDTO signUpRequestDTO){
        User user = userService.signup(signUpRequestDTO.getName(),signUpRequestDTO.getEmail(),signUpRequestDTO.getPassword());
        UserDTO userDTO =  UserDTO.from(user);
        return userDTO;
    }

    @PatchMapping("/logout")
    public void logout(@RequestBody LogoutRequestDTO logoutRequestDTO){
         userService.logout(logoutRequestDTO.getToken());
    }

    @GetMapping("/validate/{token}")
    public UserDTO validateToken(@PathVariable String token){
        User user =  userService.validateToken(token);
        UserDTO userDTO = UserDTO.from(user);
        return userDTO;
    }
}
