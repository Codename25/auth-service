package vistager.service;

import vistager.model.JwtToken;

import java.util.List;

public interface JwtTokenService {
    JwtToken save(JwtToken jwtToken);

    boolean isTokenExpired(String token);

    List<JwtToken> findAllValidTokensByUser(Long id);

    void saveAll(List<JwtToken> tokens);
}
