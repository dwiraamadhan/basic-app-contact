package practice_spring.basic_app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import practice_spring.basic_app.dto.*;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.repository.ContactRepository;
import practice_spring.basic_app.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AppUserControllerTest {

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
    void testRegisterSuccess() throws Exception{
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("dwiraamadhan");
        request.setPassword("12345");
        request.setName("Dwi Ramadhan");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){
            });

            assertEquals("Successful", response.getData());
        });
    }

    @Test
    void testRegisterFailed() throws Exception{
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){
            });

            assertNotNull(response.getError());
        });
    }

    @Test
    void testRegisterDuplicate() throws Exception{
        AppUser user = new AppUser();
        user.setUsername("dwiraamadhan");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        user.setName("Dwi Ramadhan");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("dwiraamadhan");
        request.setPassword("12345");
        request.setName("Dwi Ramadhan");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>(){
            });

            assertNotNull(response.getError());
        });
    }


    @Test
    void getUserUnauthorized() throws Exception{
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "not found")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getError());
        });
    }


    @Test
    void getUserUnauthorizedTokenNotSend() throws Exception{
        mockMvc.perform(
                get("/api/users/current")
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
    void getUserSuccess() throws Exception{
        AppUser user = new AppUser();
        user.setUsername("dwiraamadhan");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        user.setName("Dwi Ramadhan");
        user.setToken("token12345");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "token12345")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AppUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getError());
            assertEquals("dwiraamadhan", response.getData().getUsername());
            assertEquals("Dwi Ramadhan", response.getData().getName());
        });
    }


    @Test
    void getUserTokenExpired() throws Exception{
        AppUser user = new AppUser();
        user.setUsername("dwiraamadhan");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        user.setName("Dwi Ramadhan");
        user.setToken("token12345");
        user.setTokenExpiredAt(System.currentTimeMillis() - 100000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "token12345")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getError());
        });
    }


    @Test
    void updateUserUnauthorized() throws Exception{
        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                patch("/api/users/current")
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
    void updateUserSuccess() throws Exception{
        AppUser user = new AppUser();
        user.setUsername("dwiraamadhan");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        user.setName("Dwi Ramadhan");
        user.setToken("token12345");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);
        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Jessica");
        request.setPassword("54321");

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN","token12345")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AppUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getError());
            assertEquals("Jessica", response.getData().getName());
            assertEquals("dwiraamadhan", response.getData().getUsername());

            AppUser userDB = userRepository.findById("dwiraamadhan").orElse(null);
            assertNotNull(userDB);
            assertTrue(BCrypt.checkpw("54321", userDB.getPassword()));

        });
    }

}