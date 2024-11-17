package vistager.service.impl;

import vistager.exception_handling.exception.NoEntityFoundException;
import vistager.model.ConfirmationToken;
import vistager.repository.ConfirmationTokenRepository;
import vistager.service.ConfirmationTokenService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public ConfirmationToken save(ConfirmationToken token) {
        if (token.getId() != null && confirmationTokenRepository.existsById(token.getId())) {
            throw new EntityExistsException("Confirmation token with id: " + token.getId() + " already exists");
        } else if (confirmationTokenRepository.existsConfirmationTokenByToken(token.getToken())) {
            throw new EntityExistsException("Confirmation token with token name: " + token.getToken() + " already exists");
        }
        return confirmationTokenRepository.save(token);
    }

    @Override
    public ConfirmationToken findByUserId(Long id) {
        return confirmationTokenRepository.findByUserId(id).orElseThrow(() -> {
            throw new NoEntityFoundException("Confirmation token with id: " + id + " doesn't exists");
        });
    }

    @Override
    public ConfirmationToken findByTokenName(String token) {
        return confirmationTokenRepository.findByTokenName(token).orElseThrow(() -> {
            throw new NoEntityFoundException("Confirmation token with token name: " + token + " doesn't exists");
        });
    }

    @Override
    public ConfirmationToken update(ConfirmationToken token) {
        if (token.getId() == null || !confirmationTokenRepository.existsById(token.getId())) {
            throw new NoEntityFoundException("Confirmation token with id: " + token.getId() + " doesn't  exists");
        }

        return confirmationTokenRepository.save(token);
    }

    @Override
    public void makeAllTokensNotAvailableForUsage(Long userId) {
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAllByUserId(userId)
                .stream()
                .peek(e -> e.setValidForUsage(false))
                .toList();

        confirmationTokenRepository.saveAll(confirmationTokens);
    }
}
