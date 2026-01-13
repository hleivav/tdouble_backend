package se.tennis.tdouble.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.tennis.tdouble.entity.Grupp;
import se.tennis.tdouble.entity.Sasong;

import java.util.List;

@Repository
public interface GruppRepository extends JpaRepository<Grupp, Long> {
    
    List<Grupp> findBySasong(Sasong sasong);
    
    List<Grupp> findBySasongId(Long sasongId);
    
    List<Grupp> findBySasongIdOrderByGruppNummerAsc(Long sasongId);
}
