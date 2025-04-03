package practice_spring.basic_app.service;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import practice_spring.basic_app.dto.LoginUserRequest;
import practice_spring.basic_app.dto.TokenResponse;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.security.BCrypt;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private AppUserRepository userRepository;

    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        AppUser user = userRepository.findById(request.getUsername())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong"));

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(System.currentTimeMillis() + (1000*60*5));
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong");
        }
    }

    @Transactional
    public void logout(AppUser user){
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }
}
