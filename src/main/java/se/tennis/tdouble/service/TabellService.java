package se.tennis.tdouble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.tennis.tdouble.dto.TabellRadDTO;
import se.tennis.tdouble.entity.Grupp;
import se.tennis.tdouble.entity.Match;
import se.tennis.tdouble.entity.Spelare;
import se.tennis.tdouble.repository.GruppRepository;
import se.tennis.tdouble.repository.MatchRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TabellService {

    private final MatchRepository matchRepository;
    private final GruppRepository gruppRepository;
    private final SasongService sasongService;

    /**
     * Beräknar tabellen för en grupp baserat på spelade matcher
     */
    public List<TabellRadDTO> beraknaTabell(Long gruppId) {
        Grupp grupp = gruppRepository.findById(gruppId)
                .orElseThrow(() -> new RuntimeException("Grupp hittades inte: " + gruppId));

        List<Match> speladeMatcher = matchRepository.findSpeladMatcherByGrupp(gruppId);
        
        // Hitta alla unika lag (par av spelare)
        Map<String, TabellRadDTO> lagTabell = new HashMap<>();
        
        for (Match match : speladeMatcher) {
            String lag1Namn = match.getLag1Namn();
            String lag2Namn = match.getLag2Namn();

            // Skapa eller hämta tabellrader
            lagTabell.putIfAbsent(lag1Namn, new TabellRadDTO(lag1Namn));
            lagTabell.putIfAbsent(lag2Namn, new TabellRadDTO(lag2Namn));

            TabellRadDTO lag1 = lagTabell.get(lag1Namn);
            TabellRadDTO lag2 = lagTabell.get(lag2Namn);

            // Räkna spelade matcher
            lag1.setSpelade(lag1.getSpelade() + 1);
            lag2.setSpelade(lag2.getSpelade() + 1);

            // Räkna vunna/förlorade set
            lag1.setVunnaSet(lag1.getVunnaSet() + match.getLag1VunnaSet());
            lag1.setForloradeSet(lag1.getForloradeSet() + match.getLag2VunnaSet());
            lag2.setVunnaSet(lag2.getVunnaSet() + match.getLag2VunnaSet());
            lag2.setForloradeSet(lag2.getForloradeSet() + match.getLag1VunnaSet());

            // Räkna vunna/förlorade gems
            int lag1Gems = 0, lag2Gems = 0;
            for (var set : match.getSetResultat()) {
                lag1Gems += set.getLag1Gems() != null ? set.getLag1Gems() : 0;
                lag2Gems += set.getLag2Gems() != null ? set.getLag2Gems() : 0;
            }
            lag1.setVunnaGems(lag1.getVunnaGems() + lag1Gems);
            lag1.setForloradeGems(lag1.getForloradeGems() + lag2Gems);
            lag2.setVunnaGems(lag2.getVunnaGems() + lag2Gems);
            lag2.setForloradeGems(lag2.getForloradeGems() + lag1Gems);

            // Räkna vinster/förluster/oavgjort
            if (match.getLag1VunnaSet() > match.getLag2VunnaSet()) {
                lag1.setVinster(lag1.getVinster() + 1);
                lag2.setForluster(lag2.getForluster() + 1);
            } else if (match.getLag2VunnaSet() > match.getLag1VunnaSet()) {
                lag2.setVinster(lag2.getVinster() + 1);
                lag1.setForluster(lag1.getForluster() + 1);
            } else {
                lag1.setOavgjorda(lag1.getOavgjorda() + 1);
                lag2.setOavgjorda(lag2.getOavgjorda() + 1);
            }

            // Beräkna poäng baserat på valt poängsystem för säsongen
            se.tennis.tdouble.entity.ScoringSystem scoringSystem = (match.getSasong() != null && match.getSasong().getScoringSystem() != null)
                    ? match.getSasong().getScoringSystem()
                    : (sasongService.findAktiva().stream().findFirst().map(s -> s.getScoringSystem()).orElse(se.tennis.tdouble.entity.ScoringSystem.GAME_BASED));

            double[] points = beraknaMatchPoang(match, scoringSystem);
            lag1.addPoang(points[0]);
            lag2.addPoang(points[1]);
        }

        // Beräkna setdifferens och gemsdifferens (poäng är redan summerade)
        for (TabellRadDTO rad : lagTabell.values()) {
            rad.beraknaPoangOchDifferens();
        }

        // Sortera tabellen
        List<TabellRadDTO> sortedTabell = new ArrayList<>(lagTabell.values());
        sortedTabell.sort((a, b) -> {
            // 1. Poäng (högst först)
            int cmp = Double.compare(b.getPoang(), a.getPoang());
            if (cmp != 0) return cmp;
            // 2. Setdifferens
            if (b.getSetDifferens() != a.getSetDifferens()) {
                return b.getSetDifferens() - a.getSetDifferens();
            }
            // 3. Gemsdifferens
            return b.getGemsDifferens() - a.getGemsDifferens();
        });

        // Sätt placering
        for (int i = 0; i < sortedTabell.size(); i++) {
            sortedTabell.get(i).setPlacering(i + 1);
        }

        return sortedTabell;
    }

    private double[] beraknaMatchPoang(Match match, se.tennis.tdouble.entity.ScoringSystem scoringSystem) {
        double p1 = 0.0;
        double p2 = 0.0;

        // Walkover: behandla som 2-0 i set
        if (match.isWalkover()) {
            int winner = match.getLag1VunnaSet() > match.getLag2VunnaSet() ? 1 : 2;
            switch (scoringSystem) {
                case GAME_BASED:
                    if (winner == 1) p1 = 4.0; else p2 = 4.0;
                    break;
                case SET_BASED:
                    if (winner == 1) p1 = 2.0; else p2 = 2.0;
                    break;
                case MOMENTUM:
                    if (winner == 1) p1 = 4.0; else p2 = 4.0;
                    break;
            }
            return new double[]{p1, p2};
        }

        // Samla information om sets och games
        int totalG1 = 0, totalG2 = 0;
        int completed1 = 0, completed2 = 0;
        List<se.tennis.tdouble.entity.SetResultat> incompleteSets = new ArrayList<>();
        for (var set : match.getSetResultat()) {
            Integer g1 = set.getLag1Gems();
            Integer g2 = set.getLag2Gems();
            if (g1 != null && g2 != null) {
                totalG1 += g1;
                totalG2 += g2;
                if (isSetCompleted(g1, g2)) {
                    if (g1 > g2) completed1++; else if (g2 > g1) completed2++;
                } else {
                    incompleteSets.add(set);
                }
            }
        }

        switch (scoringSystem) {
            case GAME_BASED:
                p1 = totalG1 + (completed1 * 2);
                p2 = totalG2 + (completed2 * 2);
                if (completed1 > completed2) p1 += 2.0;
                else if (completed2 > completed1) p2 += 2.0;
                break;
            case SET_BASED:
                p1 = completed1 * 1.0;
                p2 = completed2 * 1.0;
                for (var set : incompleteSets) {
                    // Oavslutat set räknas som 0.5-0.5 oavsett ledare
                    p1 += 0.5;
                    p2 += 0.5;
                }
                break;
            case MOMENTUM:
                p1 = completed1 * 2.0;
                p2 = completed2 * 2.0;
                for (var set : incompleteSets) {
                    Integer g1 = set.getLag1Gems();
                    Integer g2 = set.getLag2Gems();
                    if (g1 == null || g2 == null) {
                        p1 += 0.5; p2 += 0.5; // safety, shouldn't happen
                    } else if (g1 > g2) {
                        p1 += 1.0;
                    } else if (g2 > g1) {
                        p2 += 1.0;
                    } else {
                        p1 += 0.5; p2 += 0.5;
                    }
                }
                break;
        }

        return new double[]{p1, p2};
    }

    private boolean isSetCompleted(int g1, int g2) {
        int max = Math.max(g1, g2);
        int min = Math.min(g1, g2);
        int diff = Math.abs(g1 - g2);
        if ((max >= 6 && diff >= 2) || (max == 7 && min == 6)) return true;
        return false;
    }
}
