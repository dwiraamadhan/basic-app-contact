package practice_spring.basic_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import practice_spring.basic_app.dto.LoginUserRequest;
import practice_spring.basic_app.dto.TokenResponse;
import practice_spring.basic_app.dto.WebResponse;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.service.AuthService;

@RestController
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
        TokenResponse tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }


    @DeleteMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(AppUser user){
        authService.logout(user);
        return WebResponse.<String>builder().data("Successful").build();
    }
}
