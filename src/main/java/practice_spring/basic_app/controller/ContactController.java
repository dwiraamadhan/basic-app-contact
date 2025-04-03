package practice_spring.basic_app.controller;


import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import practice_spring.basic_app.dto.*;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.service.ContactService;

import java.util.List;

@RestController
@AllArgsConstructor
public class ContactController {

    private ContactService contactService;

    @PostMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> createContact(AppUser user, @RequestBody CreateContactRequest request){
        ContactResponse contactResponse = contactService.createContact(user, request);
        return WebResponse.<ContactResponse>builder()
                .data(contactResponse)
                .build();
    }

    @GetMapping(
            path = "/api/contacts/{idContact}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> getContact(AppUser user, @PathVariable("idContact") String idContact){
        ContactResponse contactResponse = contactService.getContact(user, idContact);
        return WebResponse.<ContactResponse>builder()
                .data(contactResponse)
                .build();
    }

    @PutMapping(
            path = "/api/contacts/{idContact}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> updateContact(AppUser user,
                                                      @RequestBody UpdateContactRequest request,
                                                      @PathVariable("idContact")String idContact){
        request.setId(idContact);

        ContactResponse contactResponse = contactService.updateContact(user, request);
        return WebResponse.<ContactResponse>builder()
                .data(contactResponse)
                .build();
    }


    @DeleteMapping(
            path = "/api/contacts/{idContact}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteContact(AppUser user, @PathVariable("idContact") String idContact){
        contactService.deleteContact(user, idContact);
        return WebResponse.<String>builder()
                .data("successful")
                .build();
    }


    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(AppUser user,
                                               @RequestParam(value = "name", required = false) String name,
                                               @RequestParam(value = "email", required = false) String email,
                                               @RequestParam(value = "phone", required = false) String phone,
                                               @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){

        SearchContactRequest request = SearchContactRequest.builder()
                .page(page)
                .size(size)
                .name(name)
                .email(email)
                .phone(phone)
                .build();

        Page<ContactResponse> contactResponses = contactService.search(user, request);
        return WebResponse.<List<ContactResponse>>builder()
                .data(contactResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(contactResponses.getNumber())
                        .totalPage(contactResponses.getTotalPages())
                        .size(contactResponses.getSize())
                        .build())
                .build();

    }

}
