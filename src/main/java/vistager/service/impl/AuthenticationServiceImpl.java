package vistager.service.impl;

import vistager.exception_handling.exception.CustomAuthException;
import vistager.exception_handling.exception.LoginAuthException;
import vistager.feign.EmailFeignClient;
import vistager.feign.request.ConfirmationTokenReqDto;
import vistager.model.ConfirmationToken;
import vistager.model.JwtToken;
import vistager.security.CustomUserDetailsService;
import vistager.security.jwt.JwtService;
import vistager.model.User;
import vistager.model.LoginProvider;
import vistager.service.AuthenticationService;
import vistager.service.ConfirmationTokenService;
import vistager.service.JwtTokenService;
import vistager.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final String BEARER = "Bearer_";
    @Value("${confirmation.token.timeToLive}")
    private Integer TIME_TO_LIVE_OF_CONFIRMATION_TOKEN; // minutes
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailFeignClient emailFeignClient;

    @Override
    @Transactional
    public String login(User user) {
        // let spring check if user credentials are valid
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            // by extracting principal you're checking if user is present in system
            auth.getPrincipal();

            // TODO: replace this logic with custom method

            user.setId(userService.findByEmail(user.getEmail()).getId());

            return generateJwtToken(user);
        } catch (AuthenticationException e) {
            throw new LoginAuthException("login or password is incorrect");
        }
    }

    @Override
    @Transactional
    public String emailVerification(Long userId, String token) {
        User user = userService.findByIdNotSecure(userId);
        ConfirmationToken tokenFromDb = confirmationTokenService.findByTokenName(token);

        if (!Objects.equals(userId, tokenFromDb.getUser().getId())) {
            throw new CustomAuthException("Passed id: " + userId + " isn't matching with id in db");
        }

        // check if token is not out of date
        if (checkValidityOfToken(tokenFromDb)) {
            throw new CustomAuthException("Token: " + token + " is outdated");
        }

        // update this token as used
        tokenFromDb.setUsed(true);
        tokenFromDb.setValidForUsage(false);
        confirmationTokenService.update(tokenFromDb);

        // make all other tokens not valid for usage
        confirmationTokenService.makeAllTokensNotAvailableForUsage(userId);

        // update user's isEmailVerified
        user.setEmailVerified(true);
        userService.update(user);

        // create jwt
        return generateJwtToken(user);
    }

    @Override
    @Transactional
    public void resendEmailVerification(Long userId) {
        User user = userService.findByIdNotSecure(userId);
        confirmationTokenService.makeAllTokensNotAvailableForUsage(user.getId());
        sendVerificationEmail(user);
    }

    @Override
    public void sendResetPasswordEmail(String email) {
        User user = userService.findByEmail(email);
        confirmationTokenService.makeAllTokensNotAvailableForUsage(user.getId());

        ConfirmationToken confirmationToken = buildConfirmationToken(user);
        confirmationTokenService.save(confirmationToken);

        // send verification token to email
        ConfirmationTokenReqDto tokenReqDto = buildConfirmationTokenReqDto(user.getEmail(), confirmationToken.getToken(), user.getId());
        CompletableFuture.runAsync(() -> emailFeignClient.sendResetPasswordEmail(tokenReqDto));

    }

    @Override
    public String resetPassword(String email, String token, String password) {
        User user = userService.findByEmail(email);
        ConfirmationToken tokenFromDb = confirmationTokenService.findByTokenName(token);

        if (!Objects.equals(user.getId(), tokenFromDb.getUser().getId())) {
            throw new CustomAuthException("Passed token is registered for different user");
        }

        // check if token is not out of date
        if (checkValidityOfToken(tokenFromDb)) {
            throw new CustomAuthException("Token: " + token + " is outdated");
        }

        tokenFromDb.setUsed(true);
        tokenFromDb.setValidForUsage(false);
        confirmationTokenService.update(tokenFromDb);

        // make all other tokens not valid for usage
        confirmationTokenService.makeAllTokensNotAvailableForUsage(user.getId());

        // reset password
        user.setPassword(passwordEncoder.encode(password));
        userService.update(user);

        return generateJwtToken(user);
    }

    @Override
    @Transactional
    public User register(User user) {
        // create and save user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider(LoginProvider.APP);
        user.setCreatedAt(LocalDateTime.now());
        user.setId(userService.save(user).getId());

        // send verification email
        sendVerificationEmail(user);

        return user;
    }

    private void sendVerificationEmail(User user) {
        ConfirmationToken confirmationToken = buildConfirmationToken(user);
        confirmationTokenService.save(confirmationToken);

        // send verification token to email
        ConfirmationTokenReqDto tokenReqDto = buildConfirmationTokenReqDto(user.getEmail(), confirmationToken.getToken(), user.getId());
        CompletableFuture.runAsync(() -> emailFeignClient.sendEmail(tokenReqDto));
    }

    private ConfirmationTokenReqDto buildConfirmationTokenReqDto(String email, String token, Long userId) {
        return ConfirmationTokenReqDto.builder()
                .receiver(email)
                .token(token)
                .userId(userId)
                .build();
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

    private void expireAllTokensForUser(User user) {
        List<JwtToken> validJwtTokensForUser = jwtTokenService.findAllValidTokensByUser(user.getId());

        List<JwtToken> expiredJwtTokens = validJwtTokensForUser.stream()
                .peek(t -> t.setExpired(true))
                .toList();

        jwtTokenService.saveAll(expiredJwtTokens);
    }

    private ConfirmationToken buildConfirmationToken(User user) {
        return ConfirmationToken.builder()
                .token(generateConfirmationToken())
                .createdAt(LocalDateTime.now())
                .validForUsage(true)
                .user(user)
                .build();
    }

    private String generateConfirmationToken() {
        return UUID.randomUUID().toString();
    }

    private boolean checkValidityOfToken(ConfirmationToken token) {
        return !token.getCreatedAt().plusMinutes(TIME_TO_LIVE_OF_CONFIRMATION_TOKEN).isAfter(LocalDateTime.now());
    }

    private String generateJwtToken(User user) {
        UserDetails userDetails = customUserDetailsService.getUserDetails(user);

        String jwtToken = jwtService.generateToken(userDetails);
        expireAllTokensForUser(user);
        saveToken(user, jwtToken);
        return BEARER + jwtToken;
    }
}
