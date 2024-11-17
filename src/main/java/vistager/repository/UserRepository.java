package vistager.repository;

import vistager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "FROM User u WHERE u.isEmailVerified = TRUE AND u.email = :email")
    Optional<User> findByEmail(String email);

    @Query(value = "FROM User u WHERE u.isEmailVerified = TRUE AND u.id = :id")
    Optional<User> findById(Long id);

    @Query(value = "FROM User u WHERE u.isEmailVerified = FALSE AND u.id = :id")
    Optional<User> findByIdNotSecure(Long id);

    boolean existsUserByEmail(String email);
}
