package ee.ivkhk.jwtsecurity.repository;

import ee.ivkhk.jwtsecurity.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
