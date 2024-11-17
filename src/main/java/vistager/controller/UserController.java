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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final GenericDtoMapper<LoginReqDto, UserRespDto, User> userDtoMapper;
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserRespDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userDtoMapper.toDto(userService.findById(id)));
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserRespDto> getByEmail(@RequestParam("id") @NotBlank String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userDtoMapper.toDto(userService.findByEmail(email)));
    }
}
