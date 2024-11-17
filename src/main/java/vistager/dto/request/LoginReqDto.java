package vistager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {
    @NotBlank(message = "email can't be empty")
    @Email(message = "invalid email provided")
    private String email;
    @NotEmpty(message = "password can't be empty")
    private String password;
}
