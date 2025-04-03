package practice_spring.basic_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import practice_spring.basic_app.entity.AppUser;
import practice_spring.basic_app.entity.Contact;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String>, JpaSpecificationExecutor<Contact> {
    Optional<AppUser> findFirstByToken(String token);
}
