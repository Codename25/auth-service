package vistager.security.handler;

import vistager.model.JwtToken;
import vistager.repository.JwtTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final JwtTokenRepository jwtTokenRepository;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;

        if (authHeader == null ||!authHeader.startsWith("Bearer_")) {
            return;
        }

        jwt = authHeader.substring(7);
        JwtToken storedToken = jwtTokenRepository.findByToken(jwt)
                .orElse(null);

        if (storedToken != null) {
            storedToken.setExpired(true);
            jwtTokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}
