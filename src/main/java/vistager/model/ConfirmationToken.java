package vistager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "confirmation_token")
@Entity
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "confirmation_token_id_seq")
    @SequenceGenerator(sequenceName = "confirmation_token_id_seq", name = "confirmation_token_id_seq", allocationSize = 1)
    private Integer id;
    @Column(unique = true,
            updatable = false)
    private String token;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private boolean used;
    @Column(name = "valid_for_usage")
    private boolean validForUsage;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
