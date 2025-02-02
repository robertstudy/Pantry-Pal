package entities.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find a user by their username
    User findByUsername(String username);

    // Delete a user by their username
    void deleteByUsername(String username);

    User findByUid(int uid);
}
