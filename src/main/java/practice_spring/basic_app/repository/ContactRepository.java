package practice_spring.basic_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.entity.Contact;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    Optional<Contact> findFirstByUserAndId(AppUser user, String id);

    Page<Contact> findAll(Specification<Contact> specification, Pageable pageable);
}
