package practice_spring.basic_app.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import practice_spring.basic_app.dto.AppUserResponse;
import practice_spring.basic_app.dto.RegisterUserRequest;
import practice_spring.basic_app.dto.UpdateUserRequest;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.security.BCrypt;

import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class AppUserService {
    private AppUserRepository appUserRepository;

    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request){

        validationService.validate(request);

        //check username in database
        if(appUserRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already used");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        appUserRepository.save(user);
    }


    public AppUserResponse getUser(AppUser user){
        return AppUserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }


    @Transactional
    public AppUserResponse updateUser(AppUser user, UpdateUserRequest request){
        validationService.validate(request);
        log.info("REQUEST {}", request);

        if(Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }

        if(Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        appUserRepository.save(user);
        log.info("USER {}", user.getName());

        return AppUserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

}
