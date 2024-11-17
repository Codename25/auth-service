package vistager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailReqDto {
    @NotBlank(message = "email can't be empty")
    @Email(message = "invalid email provided")
    private String email;
}
