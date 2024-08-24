package com.parimal.linguaconnect.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.parimal.linguaconnect.entity.Token;
import com.parimal.linguaconnect.entity.UserInfo;
import com.parimal.linguaconnect.repository.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public Token save(Token token){
        return tokenRepository.save(token);
    }
    
    public Token save(UserInfo userInfo,String jwtToken){
        Token token=Token.builder()
            .userInfo(userInfo)
            .token(jwtToken)
            .revoked(false)
            .expired(false)
            .build();
       return tokenRepository.save(token);
    }

    public void revokeAllUserTokens(UserInfo userInfo){
        var validUserTokens=tokenRepository.findAllValidTokensByUser(userInfo.getId());

        if(validUserTokens.isEmpty()) return;

        validUserTokens.forEach(t->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        
        tokenRepository.saveAll(validUserTokens);
    }

    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
