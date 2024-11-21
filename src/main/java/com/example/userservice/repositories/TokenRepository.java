package com.example.userservice.repositories;

import com.example.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    Optional<Token> findByValueAndDeletedAndExpiryAtGreaterThan(String tokenValue, boolean deleted, Date currentTime);

    Token save(Token token);

}
