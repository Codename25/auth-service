package vistager.mapper.impl;

import vistager.dto.request.LoginReqDto;
import vistager.dto.response.UserRespDto;
import vistager.mapper.GenericDtoMapper;
import vistager.model.User;
import org.springframework.stereotype.Component;

@Component
public class LoginRequestDtoMapperImpl implements GenericDtoMapper<LoginReqDto, UserRespDto, User> {
    @Override
    public User toModel(LoginReqDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    @Override
    public UserRespDto toDto(User user) {
        UserRespDto dto = new UserRespDto();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
