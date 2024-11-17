package vistager.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "jwt_token")
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jwt_token_id_seq")
    @SequenceGenerator(sequenceName = "jwt_token_id_seq", name = "jwt_token_id_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true)
    public String token;
    public boolean expired;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}
