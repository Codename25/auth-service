package vistager.service.impl;

import vistager.model.AppUser;
import vistager.model.JwtToken;
import vistager.model.User;
import vistager.model.LoginProvider;
import vistager.repository.UserRepository;
import vistager.service.JwtTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsManager {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository dbUserRepository;
    private final JwtTokenService jwtTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return dbUserRepository
                .findByEmail(email)
                .map(user -> AppUser
                        .builder()
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .password(user.getPassword())
                        .name(user.getFirstname() + " " + user.getLastname())
                        .email(user.getEmail())
                        .provider(user.getProvider())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found!", email)));
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcLoginHandler() {
        return userRequest -> {
            LoginProvider provider = getLoginProvider(userRequest);
            OidcUserService delegate = new OidcUserService();
            OidcUser oidcUser = delegate.loadUser(userRequest);
            AppUser appUser = AppUser
                    .builder()
                    .provider(provider)
                    .name(oidcUser.getFullName())
                    .email(oidcUser.getEmail())
                    .userId(oidcUser.getName())
                    .imageUrl(oidcUser.getAttribute("picture"))
                    .firstname(oidcUser.getGivenName())
                    .lastname(oidcUser.getFamilyName())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .authorities(oidcUser.getAuthorities())
                    .attributes(oidcUser.getAttributes())
                    .build();

            saveOauth2AppUser(appUser);

            return appUser;
        };
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2LoginHandler() {
        return userRequest -> {
            LoginProvider provider = getLoginProvider(userRequest);
            DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            AppUser appUser = AppUser
                    .builder()
                    .firstname(oAuth2User.getAttribute("given_name"))
                    .lastname(oAuth2User.getAttribute("family_name"))
                    .provider(provider)
                    .name(oAuth2User.getAttribute("name"))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .userId(oAuth2User.getAttribute("sub"))
                    .imageUrl(oAuth2User.getAttribute("picture"))
                    .authorities(oAuth2User.getAuthorities())
                    .attributes(oAuth2User.getAttributes())
                    .email(oAuth2User.getAttribute("email"))
                    .build();

            saveOauth2AppUser(appUser);

            return appUser;
        };
    }

    private LoginProvider getLoginProvider(OAuth2UserRequest userRequest) {
        return LoginProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
    }


    private void saveOauth2AppUser(AppUser appUser) {
        User user = createUser(appUser);

//        List<AuthorityEntity> authorities = user
//                .authorities
//                .stream()
//                .map(a -> saveAuthorityIfNotExists(a.getAuthority(), user.getProvider()))
//                .toList();
//
//        userEntity.mergeAuthorities(authorities);

        User userFromDb = dbUserRepository.save(user);
        expireAllValidJwtTokensForUser(userFromDb);
    }

    private User createUser(AppUser user) {
        return dbUserRepository
                .findByEmail(user.getEmail())
                .orElseGet(() -> dbUserRepository
                        .save(User
                                .builder()
                                .email(user.getEmail())
                                .firstname(user.getFirstname())
                                .lastname(user.getLastname())
                                .password(user.getPassword())
                                .provider(user.getProvider())
                                .createdAt(LocalDateTime.now())
                                .isEmailVerified(true)
                                .isLetterRecipient(true)
                                .build()
                        ));
    }

    private void expireAllValidJwtTokensForUser(User user) {
        List<JwtToken> jwtTokens = jwtTokenService.findAllValidTokensByUser(user.getId())
                .stream()
                .peek(t -> t.setExpired(true))
                .toList();

        jwtTokenService.saveAll(jwtTokens);
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String email) {
        return false;
    }
}
