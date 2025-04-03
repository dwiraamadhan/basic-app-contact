package practice_spring.basic_app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import practice_spring.basic_app.dto.ContactResponse;
import practice_spring.basic_app.dto.CreateContactRequest;
import practice_spring.basic_app.dto.UpdateContactRequest;
import practice_spring.basic_app.dto.WebResponse;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.entity.Contact;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.repository.ContactRepository;
import practice_spring.basic_app.security.BCrypt;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        AppUser user = new AppUser();
        user.setUsername("dwiraamadhan");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        user.setName("Dwi Ramadhan");
        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);

        userRepository.save(user);
    }


    @Test
    void createContactBadRequest() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setEmail("format email salah");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getError());
        });
    }


    @Test
    void createContactSuccess() throws Exception{
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("Dwi");
        request.setLastName("Ramadhan");
        request.setEmail("dwi@example.com");
        request.setPhone("0811233445");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });

            assertNull(response.getError());
            assertEquals("Dwi", response.getData().getFirstName());
            assertEquals("Ramadhan", response.getData().getLastName());
            assertEquals("dwi@example.com", response.getData().getEmail());
            assertEquals("0811233445", response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void getContactNotFound() throws Exception{
        mockMvc.perform(
                get("/api/contacts/1245679") //id tidak ada
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            assertNotNull(response.getError());
        });
    }


    @Test
    void getContact() throws Exception{
        AppUser user = userRepository.findById("dwiraamadhan").orElse(null);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Dwi");
        contact.setLastName("Ramadhan");
        contact.setEmail("dwi@example.com");
        contact.setPhone("08123456789");
        contact.setUser(user);
        contactRepository.save(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")

        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });

            assertNull(response.getError());
            assertEquals(contact.getId(), response.getData().getId());
            assertEquals("Dwi", response.getData().getFirstName());
            assertEquals("Ramadhan", response.getData().getLastName());
            assertEquals("dwi@example.com", response.getData().getEmail());
            assertEquals("08123456789", response.getData().getPhone());
        });
    }


    @Test
    void updateContactBadRequest() throws Exception{
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Dwi");
        request.setEmail("format email salah");

        mockMvc.perform(
                put("/api/contacts/1234")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            assertNotNull(response.getError());
        });
    }

    @Test
    void updateContactSuccess() throws Exception{
        AppUser user = userRepository.findById("dwiraamadhan").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Dwi");
        contact.setLastName("Ramadhan");
        contact.setEmail("dwi@example.com");
        contact.setPhone("08123456789");
        contactRepository.save(contact);
        log.info("contactID: {}", contact.getId());

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("Jessie");
        request.setLastName("Ramadhan");
        request.setEmail("jessie@example.com");
        request.setPhone("0213456789");

        mockMvc.perform(
                put("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });

            assertNull(response.getError());
            assertEquals(request.getFirstName(), response.getData().getFirstName());
            assertEquals(request.getLastName(), response.getData().getLastName());
            assertEquals(request.getEmail(), response.getData().getEmail());
            assertEquals(request.getPhone(), response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));
        });
    }


    @Test
    void deleteContactNotFound() throws Exception{
        mockMvc.perform(
                delete("/api/contacts/1234567")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getError());
        });
    }


    @Test
    void deleteContactSuccess() throws Exception{
        AppUser user = userRepository.findById("dwiraamadhan").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setUser(user);
        contact.setFirstName("Dwi");
        contact.setLastName("Ramadhan");
        contact.setEmail("dwi@example.com");
        contact.setPhone("08123456789");
        contactRepository.save(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")

        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNull(response.getError());
            assertEquals("successful", response.getData());

        });
    }



    @Test
    void searchNotFound() throws Exception{

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {
            });

            assertEquals(0, response.getData().size());
            assertEquals(0, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }

    @Test
    void searchSuccess() throws Exception{
        AppUser user = userRepository.findById("dwiraamadhan").orElseThrow();

        for(int i=0; i<20; i++){
            Contact contact = new Contact();
            contact.setId(UUID.randomUUID().toString());
            contact.setUser(user);
            contact.setFirstName("Dwi" + i);
            contact.setLastName("Ramadhan");
            contact.setEmail("dwi@example.com");
            contact.setPhone("08123456789");
            contactRepository.save(contact);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("name", "Dwi")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {
            });

            assertEquals(10, response.getData().size());
            assertEquals(2, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("email", "example.com")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {
            });

            assertEquals(10, response.getData().size());
            assertEquals(2, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "12345")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {
            });

            assertEquals(10, response.getData().size());
            assertEquals(2, response.getPaging().getTotalPage());
            assertEquals(0, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("phone", "12345")
                        .queryParam("page", "100")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<List<ContactResponse>>>() {
            });

            assertEquals(0, response.getData().size());
            assertEquals(2, response.getPaging().getTotalPage());
            assertEquals(100, response.getPaging().getCurrentPage());
            assertEquals(10, response.getPaging().getSize());
        });
    }

}