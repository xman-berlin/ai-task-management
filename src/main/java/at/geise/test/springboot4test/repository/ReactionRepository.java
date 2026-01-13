package at.geise.test.springboot4test.repository;

import at.geise.test.springboot4test.domain.Reaction;
import at.geise.test.springboot4test.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    List<Reaction> findByComment(Comment comment);
    Optional<Reaction> findByCommentAndEmoji(Comment comment, String emoji);
}

