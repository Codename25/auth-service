package vistager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterReqDto {
    @NotBlank(message = "firstname can't be empty")
    private String firstname;
    @NotBlank(message = "lastname can't be empty")
    private String lastname;
    private Boolean isLetterRecipient;
    @NotBlank(message = "email can't be empty")
    @Email(message = "invalid email provided")
    private String email;
    @NotBlank(message = "password can't be empty")
    @Size(min = 8, max = 30, message = "password must have at least 8 characters")
    private String password;
}
