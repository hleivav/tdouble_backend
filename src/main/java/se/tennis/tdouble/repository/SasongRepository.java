package se.tennis.tdouble.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.tennis.tdouble.entity.Sasong;

import java.util.List;
import java.util.Optional;

@Repository
public interface SasongRepository extends JpaRepository<Sasong, Long> {
    
    List<Sasong> findByAktivTrue();
    
    List<Sasong> findByAktivFalse();
    
    Optional<Sasong> findByNamn(String namn);
    
    List<Sasong> findAllByOrderByStartDatumDesc();
}
