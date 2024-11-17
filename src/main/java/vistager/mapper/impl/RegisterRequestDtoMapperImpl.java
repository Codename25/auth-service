package vistager.mapper.impl;

import vistager.dto.request.RegisterReqDto;
import vistager.dto.response.UserRespDto;
import vistager.mapper.GenericDtoMapper;
import vistager.model.User;
import org.springframework.stereotype.Component;

@Component
public class RegisterRequestDtoMapperImpl implements GenericDtoMapper<RegisterReqDto, UserRespDto, User> {
    @Override
    public User toModel(RegisterReqDto dto) {
        User user = new User();
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setLetterRecipient(dto.getIsLetterRecipient());
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
