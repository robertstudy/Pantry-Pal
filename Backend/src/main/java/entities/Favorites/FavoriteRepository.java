package entities.Favorites;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUid(int uid);
    List<Favorite> findByRid(int rid);
    boolean existsByUidAndRid(int uid, int rid);
}
