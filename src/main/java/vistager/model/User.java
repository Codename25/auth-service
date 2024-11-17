package vistager.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"user\"")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(sequenceName = "user_id_seq", name = "user_id_seq", allocationSize = 1)
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    @Column(name = "is_letter_recipient")
    private boolean isLetterRecipient;
    @Enumerated(value = EnumType.STRING)
    private LoginProvider provider;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "is_email_verified")
    private boolean isEmailVerified;
}
