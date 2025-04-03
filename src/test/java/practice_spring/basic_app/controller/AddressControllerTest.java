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
import practice_spring.basic_app.dto.AddressResponse;
import practice_spring.basic_app.dto.CreateAddressRequest;
import practice_spring.basic_app.dto.UpdateAddressRequest;
import practice_spring.basic_app.dto.WebResponse;
import practice_spring.basic_app.entity.Address;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.entity.Contact;
import practice_spring.basic_app.repository.AddressRepository;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.repository.ContactRepository;
import practice_spring.basic_app.security.BCrypt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        AppUser user = new AppUser();
        user.setUsername("dwiraamadhan");
        user.setPassword(BCrypt.hashpw("12345", BCrypt.gensalt()));
        user.setName("Dwi Ramadhan");
        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 1000000);

        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("testId");
        contact.setFirstName("Dwi");
        contact.setLastName("Ramadhan");
        contact.setEmail("dwi@example.com");
        contact.setPhone("08123456789");
        contact.setUser(user);

        contactRepository.save(contact);
    }


    @Test
    void createAddressBadRequest() throws Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                post("/api/contacts/12345/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getError());
        });
    }


    @Test
    void createAddressSuccess() throws Exception{
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCountry("Indonesia");

        mockMvc.perform(
                post("/api/contacts/testId/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getError());
            assertEquals(request.getCountry(), response.getData().getCountry());
        });
    }


    @Test
    void getAddressNotFound() throws Exception{
        mockMvc.perform(
                get("/api/contacts/12345/adresses/54321")
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
    void getAddressSuccess() throws Exception{
        Contact contact = contactRepository.findById("testId").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setStreet("JL. XYZ");
        address.setCity("Jakarta");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12345");
        address.setContact(contact);
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/testId/adresses/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {
            });

            assertNull(response.getError());
            assertEquals(address.getId(), response.getData().getId());
            assertEquals(address.getStreet(), response.getData().getStreet());
            assertEquals(address.getCity(), response.getData().getCity());
            assertEquals(address.getProvince(), response.getData().getProvince());
            assertEquals(address.getCountry(), response.getData().getCountry());
            assertEquals(address.getPostalCode(), response.getData().getPostalCode());
        });
    }


    @Test
    void updateAddressBadRequest() throws Exception{
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCountry("");

        mockMvc.perform(
                put("/api/contacts/12345/addresses/54321")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getError());
        });
    }


    @Test
    void updateAddressSuccess() throws Exception{
        Contact contact = contactRepository.findById("testId").orElseThrow();

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setStreet("JL. XYZ");
        address.setCity("Jakarta");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12345");
        address.setContact(contact);
        addressRepository.save(address);


        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setStreet("JL. KLM");
        request.setCity("Surabaya");
        request.setProvince("Jawa Timur");
        request.setCountry("Indonesia");
        request.setPostalCode("54321");

        mockMvc.perform(
                put("/api/contacts/testId/addresses/"+ address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {
            });

            assertNull(response.getError());
            assertEquals(request.getStreet(), response.getData().getStreet());
            assertEquals(request.getCity(), response.getData().getCity());
            assertEquals(request.getProvince(), response.getData().getProvince());
            assertEquals(request.getCountry(), response.getData().getCountry());
            assertEquals(request.getPostalCode(), response.getData().getPostalCode());
            assertTrue(addressRepository.existsById(response.getData().getId()));
        });
    }


}