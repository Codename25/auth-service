package vistager.feign.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ConfirmationTokenReqDto {
    private String receiver;
    private String token;
    private Long userId;
}
