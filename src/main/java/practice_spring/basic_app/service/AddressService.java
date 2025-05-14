package practice_spring.basic_app.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import practice_spring.basic_app.dto.AddressResponse;
import practice_spring.basic_app.dto.CreateAddressRequest;
import practice_spring.basic_app.dto.UpdateAddressRequest;
import practice_spring.basic_app.entity.Address;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.entity.Contact;
import practice_spring.basic_app.repository.AddressRepository;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.repository.ContactRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AddressService {

    private AddressRepository addressRepository;

    private ContactRepository contactRepository;

    private ValidationService validationService;

    @Transactional
    public AddressResponse createAddress(AppUser user, CreateAddressRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getIdContact())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);

        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }


    @Transactional(readOnly = true)
    public AddressResponse getAddress(AppUser user, String idContact, String idAddress){
        //find contact first
        Contact contact = contactRepository.findFirstByUserAndId(user, idContact)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        //then find address
        Address address = addressRepository.findFirstByContactAndId(contact, idAddress)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }


    @Transactional
    public AddressResponse updateAddress(AppUser user, UpdateAddressRequest request){
        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getIdContact())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, request.getIdAddress())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));


        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }


    @Transactional
    public void removeAddress(AppUser user, String idContact, String idAddress){
        Contact contact = contactRepository.findFirstByUserAndId(user, idContact)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, ("Contact is not found")));

        Address address = addressRepository.findFirstByContactAndId(contact, idAddress)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, ("Address is not found")));

        addressRepository.delete(address);
    }

}
