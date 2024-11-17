package vistager.controller;

import vistager.dto.request.EmailReqDto;
import vistager.dto.request.LoginReqDto;
import vistager.dto.request.RegisterReqDto;
import vistager.dto.request.ResetPasswordReqDto;
import vistager.dto.response.UserRespDto;
import vistager.exception_handling.exception.InvalidUserIdException;
import vistager.mapper.GenericDtoMapper;
import vistager.model.User;
import vistager.service.AuthenticationService;
import vistager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final GenericDtoMapper<RegisterReqDto, UserRespDto, User> registerDtoMapper;
    private final GenericDtoMapper<LoginReqDto, UserRespDto, User> loginDtoMapper;

    @Operation(summary = "Register user")
    @ApiResponse(responseCode = "200", description = "Successful registration", content = @Content(schema = @Schema(implementation = UserRespDto.class)))
    @PostMapping("/register")
    public ResponseEntity<UserRespDto> register(@RequestBody @Valid RegisterReqDto request) {
        User user = registerDtoMapper.toModel(request);
        User userFromDb = authenticationService.register(user);

        UserRespDto authRespDto = registerDtoMapper.toDto(userFromDb);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authRespDto);
    }

    @Operation(summary = "Login user")
    @ApiResponse(responseCode = "200", description = "Successful login", content = @Content(schema = @Schema(implementation = UserRespDto.class)))
    @PostMapping("/login")
    public ResponseEntity<UserRespDto> login(@RequestBody @Valid LoginReqDto request) {
        User userWithLoginAndPassword = loginDtoMapper.toModel(request);
        String token = authenticationService.login(userWithLoginAndPassword);

        User user = userService.findByEmail(request.getEmail());
        UserRespDto authRespDto = loginDtoMapper.toDto(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(authRespDto);
    }

    @Operation(summary = "Email verification (second step)", description = "after user confirms his email account he is redirected to http://localhost:3000/ here you take params from his link and put them into this link")
    @ApiResponse(responseCode = "200", description = "Successful email verification")
    @GetMapping("/register/{userId}/email/verification")
    public ResponseEntity<String> emailVerificationDuringRegistration(
            @RequestParam("token") @NotBlank String verificationToken,
            @PathVariable("userId") Long userId)
    {
        validateUserId(userId);
        String jwtToken = authenticationService.emailVerification(userId, verificationToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .body("Successful email verification");
    }

    @Operation(summary = "Email verification (first step)",
            description = "send email verification letter on user's gmail, (also can be used as resend email verification letter, in case user didn't get it)")
    @ApiResponse(responseCode = "200", description = "Email was sent")
    @GetMapping("register/{userId}/email/resend")
    public ResponseEntity<String> resendEmailVerificationEmail(@PathVariable("userId") Long userId) {
        validateUserId(userId);
        authenticationService.resendEmailVerification(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Email was sent");
    }

    @Operation(summary = "Reset password (first step)",
            description = "user presses button reset password at localhost:3000 and letter is being sent on his email account")
    @ApiResponse(responseCode = "200", description = "Email was sent")
    @PostMapping("/password/email/reset")
    public ResponseEntity<String> sendEmailForResettingPassword(@RequestBody @Valid EmailReqDto dto) {
        authenticationService.sendResetPasswordEmail(dto.getEmail());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Email was sent");
    }

    @Operation(summary = "Reset password (second step)",
            description = "after user provided new password and pressed reset email, set params from to this link which you got from link that redirected user to your page")
    @ApiResponse(responseCode = "200", description = "Password was updated")
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") @NotBlank String token,
            @Valid @RequestBody ResetPasswordReqDto dto
    ) {
        String jwtToken = authenticationService.resetPassword(
                dto.getEmail(),
                token,
                dto.getPassword());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .body("Password was updated");
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId < 0) {
            throw new InvalidUserIdException("Invalid id: " + userId + " was passed to url");
        }
    }
}
