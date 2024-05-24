package ee.ivkhk.jwtsecurity.repository;

import ee.ivkhk.jwtsecurity.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface OurUserRepo extends JpaRepository<OurUsers, Long> {
    Optional<OurUsers> findByEmail(String email);
}
