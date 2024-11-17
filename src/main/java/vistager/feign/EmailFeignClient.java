package vistager.feign;

import vistager.feign.request.ConfirmationTokenReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(value = "email", url = "http://email-service:8083")
public interface EmailFeignClient {

    @PostMapping("/api/email/auth/validation")
    void sendEmail(@RequestBody ConfirmationTokenReqDto dto);

    @PostMapping("/api/email/password/reset")
    void sendResetPasswordEmail(@RequestBody ConfirmationTokenReqDto dto);
}
