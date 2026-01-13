package at.geise.test.springboot4test.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reactions")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    Comment comment;

    String emoji;

    Integer count = 0;

    LocalDateTime createdAt = LocalDateTime.now();

    public Reaction(Comment comment, String emoji) {
        this.comment = comment;
        this.emoji = emoji;
        this.count = 1;
    }
}

