package vistager.security.handler;

import vistager.model.JwtToken;
import vistager.model.User;
import vistager.repository.JwtTokenRepository;
import vistager.security.CustomUserDetailsService;
import vistager.security.jwt.JwtService;
import vistager.service.JwtTokenService;
import vistager.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Value("${auth.redirect.success}")
    private String SUCCESS_URI_REDIRECT;
    private static final String BEARER_TOKEN = "Bearer_";
    private final JwtService jwtService;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        User user = userService.findByEmail(authentication.getName());
        UserDetails userDetails = customUserDetailsService.getUserDetails(user);

        String token = jwtService.generateToken(userDetails);
        saveToken(user, token);

        getRedirectStrategy().sendRedirect(request, response, getRedirectUrl(token, user.getId()));
    }

    private void saveToken(User user, String token) {
        JwtToken jwtToken = buildJwtToken(user, token);
        jwtTokenService.save(jwtToken);
    }

    private JwtToken buildJwtToken(User user, String token) {
        return JwtToken
                .builder()
                .token(token)
                .expired(false)
                .user(user)
                .build();
    }

    private String getRedirectUrl(String jwtToken, Long userId) {
        return SUCCESS_URI_REDIRECT + "?jwt=" + BEARER_TOKEN + jwtToken + "&userId=" + userId;
    }
}
