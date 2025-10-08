package Food_Orders.Repository;

import Food_Orders.Entity.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity,Long> {
    Optional<OtpEntity> findTopByEmailOrderByGeneratedAtDesc(String email);
}
