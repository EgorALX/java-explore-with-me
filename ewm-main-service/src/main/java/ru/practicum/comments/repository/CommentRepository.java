package ru.practicum.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comments.model.Comment;
import ru.practicum.users.model.User;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventId(Long eventId, PageRequest pageRequest);

    List<Comment> findAllByAuthor(User authorId, PageRequest pageRequest);

    void deleteAllByAuthor(User authorId);
}
