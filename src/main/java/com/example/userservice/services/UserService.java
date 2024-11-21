package com.example.userservice.services;

import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,TokenRepository tokenRepository){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }
    public Token login(String email,String password){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
           throw new RuntimeException("User with the email "+ email +" is not found in DB.");
        }
        User user = optionalUser.get();
        if(bCryptPasswordEncoder.matches(password,user.getHaashedpassword())){
            Token token = createToken(user);
            Token savedToken = tokenRepository.save(token);
            return savedToken;
        }
        return null;
    }
    public User signup(String name,String email,String password){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHaashedpassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }
    public void logout(String tokenValue){
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(tokenValue,false,new Date());
        if(optionalToken.isEmpty()) return;
        Token token = optionalToken.get();
        token.setDeleted(true);
        tokenRepository.save(token);
    }
    public User validateToken(String tokenValue){
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(tokenValue,false,new Date());
        if(optionalToken.isEmpty()) return null;
        return optionalToken.get().getUser();
    }
    private Token createToken(User user){
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAfterCurrentTime = today.plusDays(30);
        Date expiryAt = Date.from(thirtyDaysAfterCurrentTime.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpiryAt(expiryAt);
        return token;
    }
}
