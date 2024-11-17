package vistager.service;

import vistager.model.ConfirmationToken;

public interface ConfirmationTokenService {
    ConfirmationToken save(ConfirmationToken token);

    ConfirmationToken findByUserId(Long id);

    ConfirmationToken findByTokenName(String token);

    ConfirmationToken update(ConfirmationToken token);

    void makeAllTokensNotAvailableForUsage(Long userId);
}
