package org.project.backend.repository.github;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.project.backend.credential.github.GithubCredentials;
import org.project.backend.credential.github.StringListConverter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repository_github")
public class GithubRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mainBranch;


    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> secondaryBranches;

    @CreatedDate
    @CreationTimestamp
    @JsonFormat(pattern="dd-MM-yyyy HH:mm")
    @Column(
            name = "created_at"
    )
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "credential_id", nullable = false)
    private GithubCredentials credentials;
}
