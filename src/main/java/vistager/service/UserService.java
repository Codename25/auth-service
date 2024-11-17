package vistager.service;

import vistager.model.User;

public interface UserService {
    User findByEmail(String email);

    User save(User user);

    User findById(Long id);

    boolean existsById(Long id);

    User update(User user);

    User findByIdNotSecure(Long id);

    User findByJwtToken(String jwt);
}
