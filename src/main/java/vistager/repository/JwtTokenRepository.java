package vistager.repository;

import vistager.model.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    @Query(value = "FROM JwtToken jt WHERE jt.token = :token")
    Optional<JwtToken> findByToken(String token);

    @Query(value = "FROM JwtToken jt WHERE jt.expired = false AND jt.user.id = :id")
    List<JwtToken> findAllValidTokensByUser(Long id);
}
