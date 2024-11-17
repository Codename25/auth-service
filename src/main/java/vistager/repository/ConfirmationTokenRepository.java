package vistager.repository;

import vistager.model.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Integer> {
    Boolean existsConfirmationTokenByToken(String token);

    @Query(value = "FROM ConfirmationToken ct WHERE ct.token = :token AND ct.used = FALSE AND ct.validForUsage = TRUE")
    Optional<ConfirmationToken> findByTokenName(String token);

    @Query(value = "FROM ConfirmationToken ct WHERE ct.user.id = :id AND ct.used = FALSE AND ct.validForUsage = TRUE")
    Optional<ConfirmationToken> findByUserId(Long id);

    @Query(value = "FROM ConfirmationToken ct WHERE ct.user.id = :userId AND ct.validForUsage = TRUE")
    List<ConfirmationToken> findAllByUserId(Long userId);
}
