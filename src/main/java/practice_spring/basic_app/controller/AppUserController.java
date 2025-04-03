package practice_spring.basic_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import practice_spring.basic_app.dto.AppUserResponse;
import practice_spring.basic_app.dto.RegisterUserRequest;
import practice_spring.basic_app.dto.UpdateUserRequest;
import practice_spring.basic_app.dto.WebResponse;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.service.AppUserService;

@RestController
@AllArgsConstructor
public class AppUserController {

    private AppUserService userService;

    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request){
        userService.register(request);
        return WebResponse.<String>builder().data("Successful").build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AppUserResponse> getUser(AppUser user){
        AppUserResponse userResponse = userService.getUser(user);
        return WebResponse.<AppUserResponse>builder()
                .data(userResponse)
                .build();
    }

    @PatchMapping(
            value = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AppUserResponse> updateUser(AppUser user, @RequestBody UpdateUserRequest request){
        AppUserResponse userResponse = userService.updateUser(user, request);
        return WebResponse.<AppUserResponse>builder()
                .data(userResponse)
                .build();
    }
}
