package vistager.service.impl;

import vistager.model.JwtToken;
import vistager.repository.JwtTokenRepository;
import vistager.service.JwtTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public JwtToken save(JwtToken jwtToken) {
        return jwtTokenRepository.save(jwtToken);
    }

    @Override
    public boolean isTokenExpired(String token) {
        Optional<JwtToken> jwt = jwtTokenRepository.findByToken(token);
        return jwt.map(JwtToken::isExpired).orElse(false);
    }

    @Override
    public List<JwtToken> findAllValidTokensByUser(Long id) {
        return jwtTokenRepository.findAllValidTokensByUser(id);
    }

    @Override
    public void saveAll(List<JwtToken> tokens) {
        jwtTokenRepository.saveAll(tokens);
    }


}
