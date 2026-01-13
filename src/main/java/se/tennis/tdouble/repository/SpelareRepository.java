package se.tennis.tdouble.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.tennis.tdouble.entity.Spelare;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpelareRepository extends JpaRepository<Spelare, Long> {
    
    Optional<Spelare> findByNamn(String namn);
    
    List<Spelare> findByAktivTrue();
    
    List<Spelare> findByNamnContainingIgnoreCase(String namn);
    
    @Query("SELECT s FROM Spelare s JOIN s.grupper g WHERE g.id = :gruppId")
    List<Spelare> findByGruppId(@Param("gruppId") Long gruppId);
}
