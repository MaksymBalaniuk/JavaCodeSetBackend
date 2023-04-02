package com.javacodeset.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.javacodeset.enumeration.CodeBlockType;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "CodeBlock")
@Table(name = "code_blocks")
public class CodeBlockEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private CodeBlockType type;

    @CreationTimestamp
    @Column(name = "created")
    private Instant created;

    @UpdateTimestamp
    @Column(name = "updated")
    private Instant updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @OneToMany(mappedBy = "codeBlock", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<CommentEntity> comments = new HashSet<>();

    @OneToMany(mappedBy = "codeBlock", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<EstimateEntity> estimates = new HashSet<>();

    @OneToMany(mappedBy = "codeBlock", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<ShareEntity> shares = new HashSet<>();

    @ManyToMany(mappedBy = "codeBlocks", fetch = FetchType.LAZY)
    private Set<TagEntity> tags = new HashSet<>();
}
