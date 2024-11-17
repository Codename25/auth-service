package vistager.controller;

import vistager.dto.request.LoginReqDto;
import vistager.dto.response.UserRespDto;
import vistager.mapper.GenericDtoMapper;
import vistager.model.User;
import vistager.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class ForInternalUsageController {
    private final GenericDtoMapper<LoginReqDto, UserRespDto, User> userDtoMapper;
    private final UserService userService;

    @GetMapping("/user/by-jwt")
    public ResponseEntity<UserRespDto> getByJwt(@RequestParam("jwt") @NotBlank String jwt) {
        return ResponseEntity.status(HttpStatus.OK).body(userDtoMapper.toDto(userService.findByJwtToken(jwt)));
    }
}
