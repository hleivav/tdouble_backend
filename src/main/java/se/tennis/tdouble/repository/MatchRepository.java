package se.tennis.tdouble.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.tennis.tdouble.entity.Match;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    List<Match> findBySasongId(Long sasongId);
    
    List<Match> findByGruppId(Long gruppId);
    
    List<Match> findBySasongIdAndGruppId(Long sasongId, Long gruppId);
    
    List<Match> findByMatchDatum(LocalDate datum);
    
    List<Match> findBySasongIdOrderByMatchDatumAscMatchTidAsc(Long sasongId);
    
    List<Match> findBySpeladFalseAndSasongId(Long sasongId);
    
    List<Match> findBySpeladTrueAndSasongId(Long sasongId);
    
    @Query("SELECT m FROM Match m WHERE m.sasong.id = :sasongId AND " +
           "(m.lag1Spelare1.id = :spelareId OR m.lag1Spelare2.id = :spelareId OR " +
           "m.lag2Spelare1.id = :spelareId OR m.lag2Spelare2.id = :spelareId)")
    List<Match> findBySpelareAndSasong(@Param("spelareId") Long spelareId, @Param("sasongId") Long sasongId);
    
    @Query("SELECT m FROM Match m WHERE m.grupp.id = :gruppId AND m.spelad = true")
    List<Match> findSpeladMatcherByGrupp(@Param("gruppId") Long gruppId);
}
