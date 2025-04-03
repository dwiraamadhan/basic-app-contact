package practice_spring.basic_app.service;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import practice_spring.basic_app.dto.ContactResponse;
import practice_spring.basic_app.dto.CreateContactRequest;
import practice_spring.basic_app.dto.SearchContactRequest;
import practice_spring.basic_app.dto.UpdateContactRequest;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.entity.Contact;
import practice_spring.basic_app.repository.AppUserRepository;
import practice_spring.basic_app.repository.ContactRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContactService {

    private ValidationService validationService;

    private ContactRepository contactRepository;


    @Transactional
    public ContactResponse createContact(AppUser user, CreateContactRequest request){
        validationService.validate(request);

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contact.setUser(user);

        contactRepository.save(contact);

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }


    @Transactional(readOnly = true)
    public ContactResponse getContact(AppUser user, String id){
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();

    }


    @Transactional
    public ContactResponse updateContact(AppUser user, UpdateContactRequest request){

        validationService.validate(request);

        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }


    @Transactional
    public void deleteContact(AppUser user, String id){
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not Found"));

        contactRepository.delete(contact);
    }


    private ContactResponse toContactResponse(Contact contact){
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }


    @Transactional(readOnly = true)
    public Page<ContactResponse> search(AppUser user, SearchContactRequest request){
        Specification<Contact> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("user"), user));
            if(Objects.nonNull(request.getName())){
                predicates.add(builder.or(
                        builder.like(root.get("firstName"),"%"+request.getName()+"%"),
                        builder.like(root.get("lastName"),"%"+request.getName()+"%")
                ));
            }

            if(Objects.nonNull(request.getEmail())){
                predicates.add(builder.like(root.get("email"), "%" + request.getEmail() + "%"));
            }

            if(Objects.nonNull(request.getPhone())){
                predicates.add(builder.like(root.get("phone"), "%" + request.getPhone() + "%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream()
                .map(this::toContactResponse)
                .toList();

        return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());
    }



}
