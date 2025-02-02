package entities.Images;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    Image findByIid(int iid);

    Image findByRid(int rid);

    Image findByStep(int step);

    Image findByRidAndStep(int rid, int step);

    Image findByRidAndStepIsNull(int rid);
}
