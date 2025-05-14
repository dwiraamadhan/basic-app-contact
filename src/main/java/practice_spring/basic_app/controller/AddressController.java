package practice_spring.basic_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import practice_spring.basic_app.dto.AddressResponse;
import practice_spring.basic_app.dto.CreateAddressRequest;
import practice_spring.basic_app.dto.UpdateAddressRequest;
import practice_spring.basic_app.dto.WebResponse;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.service.AddressService;

@RestController
@AllArgsConstructor
public class AddressController {

    private AddressService addressService;

    @PostMapping(
            path = "/api/contacts/{idContact}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> createAddress(AppUser user,
                                                      @RequestBody CreateAddressRequest request,
                                                      @PathVariable("idContact") String idContact){

        request.setIdContact(idContact);
        AddressResponse addressResponse = addressService.createAddress(user, request);

        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .build();
    }

    @GetMapping(
            path = "/api/contacts/{idContact}/adresses/{idAddress}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> getAddress(AppUser user,
                                                   @PathVariable("idContact") String idContact,
                                                   @PathVariable("idAddress") String idAddress){
        AddressResponse addressResponse = addressService.getAddress(user, idContact, idAddress);

        return WebResponse.<AddressResponse>builder()
                .data(addressResponse)
                .build();
    }


    @PutMapping(
            path = "/api/contacts/{idContact}/addresses/{idAddress}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> updateAddress(AppUser user,
                                                      @RequestBody UpdateAddressRequest request,
                                                      @PathVariable("idContact") String idContact,
                                                      @PathVariable("idAddress") String idAddress){

        request.setIdContact(idContact);
        request.setIdAddress(idAddress);

        AddressResponse response = addressService.updateAddress(user, request);

        return WebResponse.<AddressResponse>builder()
                .data(response)
                .build();
    }


    @DeleteMapping(
            path = ("/api/contacts/{idContact}/addresses/{idAddress}"),
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteAddress(AppUser user,
                                                      @PathVariable String idContact,
                                                      @PathVariable String idAddress){
        addressService.removeAddress(user, idContact, idAddress);
        return WebResponse.<String>builder()
                .data("Successful")
                .build();
    }

}
