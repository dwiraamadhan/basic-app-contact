package practice_spring.basic_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice_spring.basic_app.entity.Address;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.entity.Contact;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    Optional<Address> findFirstByContactAndId(Contact contact, String id);
}
