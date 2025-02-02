package entities.Ratings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByUid(int uid);
    List<Rating> findByRid(int rid);
    Rating findByUidAndRid(int uid, int rid);

    @Query("SELECT AVG(r.averageRating) FROM Recipe r WHERE r.uid = :uid")
    Double findAvgByUid(@Param("uid") int uid);
}
