package at.geise.test.springboot4test.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    Task task;

    @NotBlank
    @Size(max = 1000)
    String content;

    @NotBlank
    @Size(max = 100)
    String author = "Anonymous"; // For now, we'll use a default author

    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<Reaction> reactions = new ArrayList<>();

    public Comment(Task task, String content, String author) {
        this.task = task;
        this.content = content;
        this.author = author != null ? author : "Anonymous";
    }
}

