package blogtest.Repository;

import blogtest.Model.Post;
import blogtest.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findPostByIdAndUsers(Long id, Users users);

    List<Post> findAllByUsersOrderByCreatedDateDesc(Users users);

    List<Post> findAllByOrderByCreatedDateDesc();
}
