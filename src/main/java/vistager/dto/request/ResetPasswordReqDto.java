package vistager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordReqDto {
    @NotBlank(message = "email can't be empty")
    @Email(message = "invalid email provided")
    private String email;
    @NotBlank(message = "password can't be empty")
    @Size(min = 8, max = 30, message = "password must have at least 8 characters")
    private String password;
}
