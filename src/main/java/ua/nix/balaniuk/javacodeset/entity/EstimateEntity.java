package ua.nix.balaniuk.javacodeset.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ua.nix.balaniuk.javacodeset.enumeration.EstimateType;

import java.util.UUID;

@Getter
@Setter
@Entity(name = "Estimate")
@Table(name = "estimates")
public class EstimateEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EstimateType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", referencedColumnName = "id")
    private CodeBlockEntity codeBlock;
}
