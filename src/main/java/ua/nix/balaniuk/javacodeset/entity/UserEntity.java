package ua.nix.balaniuk.javacodeset.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ua.nix.balaniuk.javacodeset.enumeration.UserPremium;
import ua.nix.balaniuk.javacodeset.enumeration.UserStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "User")
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "premium")
    private UserPremium premium;

    @CreationTimestamp
    @Column(name = "created")
    private Instant created;

    @UpdateTimestamp
    @Column(name = "updated")
    private Instant updated;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private Set<AuthorityEntity> authorities = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<CodeBlockEntity> codeBlocks = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<CommentEntity> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<EstimateEntity> estimates = new HashSet<>();
}
