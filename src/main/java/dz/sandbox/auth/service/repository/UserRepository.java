package dz.sandbox.auth.service.repository;

import dz.sandbox.auth.service.entity.DzUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<DzUser, Long> {

  Optional<DzUser> findByUsername(String username);
  
  boolean existsByUsername(String username);
}
