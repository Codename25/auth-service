package vistager.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRespDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
}
