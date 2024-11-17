package vistager.service.impl;

import vistager.exception_handling.exception.UserExistsException;
import vistager.exception_handling.exception.NoEntityFoundException;
import vistager.model.User;
import vistager.repository.UserRepository;
import vistager.security.jwt.JwtService;
import vistager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            throw new NoEntityFoundException("User with email: " + email + " was not found");
        });
    }

    @Override
    public User save(User user) {
        if (user.getEmail() != null && userRepository.existsUserByEmail(user.getEmail())) {
            throw new UserExistsException("User with email: " + user.getEmail() + " already exists");
        } else if (user.getId() != null && userRepository.existsById(user.getId())) {
            throw new UserExistsException("User with id: " + user.getId() + " already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            throw new NoEntityFoundException("User with id: " + id + " doesn't exist");
        });
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public User update(User user) {
        if (user.getId() == null || !userRepository.existsById(user.getId())) {
            throw new NoEntityFoundException("User doesn't exist");
        }
        return userRepository.save(user);
    }

    @Override
    public User findByIdNotSecure(Long id) {
        return userRepository.findByIdNotSecure(id).orElseThrow(() -> {
            throw new NoEntityFoundException("User with id: " + id + " doesn't exist");
        });
    }

    @Override
    public User findByJwtToken(String jwt) {
        String email = jwtService.extractUserName(jwt);

        return findByEmail(email);
    }
}
