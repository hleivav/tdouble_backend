package se.tennis.tdouble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.tennis.tdouble.entity.Match;
import se.tennis.tdouble.entity.SetResultat;
import se.tennis.tdouble.repository.MatchRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;

    public List<Match> findAll() {
        return matchRepository.findAll();
    }

    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }

    public List<Match> findBySasongId(Long sasongId) {
        return matchRepository.findBySasongIdOrderByMatchDatumAscMatchTidAsc(sasongId);
    }

    public List<Match> findByGruppId(Long gruppId) {
        return matchRepository.findByGruppId(gruppId);
    }

    public List<Match> findOspelade(Long sasongId) {
        return matchRepository.findBySpeladFalseAndSasongId(sasongId);
    }

    public List<Match> findSpelade(Long sasongId) {
        return matchRepository.findBySpeladTrueAndSasongId(sasongId);
    }

    public Match save(Match match) {
        return matchRepository.save(match);
    }

    public Match rapporteraResultat(Long matchId, List<int[]> setResultat) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match hittades inte: " + matchId));

        // Rensa gamla resultat
        match.getSetResultat().clear();

        // Lägg till nya resultat
        for (int i = 0; i < setResultat.size(); i++) {
            int[] set = setResultat.get(i);
            if (set[0] >= 0 && set[1] >= 0) {
                SetResultat sr = SetResultat.builder()
                        .setNummer(i + 1)
                        .lag1Gems(set[0])
                        .lag2Gems(set[1])
                        .build();
                match.addSetResultat(sr);
            }
        }

        match.beraknaVunnaSet();
        match.setSpelad(true);

        return matchRepository.save(match);
    }

    public Match setWalkover(Long matchId, int vinnareLag) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match hittades inte: " + matchId));

        match.setWalkover(true);
        match.setSpelad(true);

        if (vinnareLag == 1) {
            match.setLag1VunnaSet(2);
            match.setLag2VunnaSet(0);
        } else {
            match.setLag1VunnaSet(0);
            match.setLag2VunnaSet(2);
        }

        return matchRepository.save(match);
    }

    public void delete(Long id) {
        matchRepository.deleteById(id);
    }
}
