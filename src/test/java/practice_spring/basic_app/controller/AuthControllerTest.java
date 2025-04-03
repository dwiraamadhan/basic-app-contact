package practice_spring.basic_app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import practice_spring.basic_app.dto.LoginUserRequest;
import practice_spring.basic_app.dto.TokenResponse;
import practice_spring.basic_app.dto.WebResponse;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();
    }

    @Test
    void loginFailedUserNotFound()throws Exception{
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("dwiraamadhan");
        request.setPassword("12345");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getError());
        });
    }


    @Test
    void loginFailedWrongPassword()throws Exception{
        AppUser user = new AppUser();
        user.setUsername("dwiraamadhan");
        user.setName("Dwi Ramadhan");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("dwiraamadhan");
        request.setPassword("wrong password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getError());
        });
    }


    @Test
    void loginSuccess()throws Exception{
        AppUser user = new AppUser();
        user.setUsername("jessie");
        user.setName("Jessica");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("jessie");
        request.setPassword("12345");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getError());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            AppUser userDB = userRepository.findById("jessie").orElse(null);
            assertNotNull(userDB);
            assertEquals(userDB.getToken(), response.getData().getToken());
            assertEquals(userDB.getTokenExpiredAt(), response.getData().getExpiredAt());
        });
    }

    @Test
    void logoutFailed()throws Exception{
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getError());
        });
    }


    @Test
    void logoutSuccess()throws Exception{
        AppUser user = new AppUser();
        user.setUsername("jessie");
        user.setName("Jessica");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getError());
            assertEquals("Successful", response.getData());

            AppUser userDB = userRepository.findById("jessie").orElse(null);
            assertNotNull(userDB);
            assertNull(userDB.getToken());
            assertNull(userDB.getTokenExpiredAt());
        });
    }

}