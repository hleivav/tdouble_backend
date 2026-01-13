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
        }
        
        // Beräkna poäng och setdifferens
        for (TabellRadDTO rad : lagTabell.values()) {
            rad.beraknaPoangOchDifferens();
        }
        
        // Sortera tabellen
        List<TabellRadDTO> sortedTabell = new ArrayList<>(lagTabell.values());
        sortedTabell.sort((a, b) -> {
            // 1. Poäng (högst först)
            if (b.getPoang() != a.getPoang()) {
                return b.getPoang() - a.getPoang();
            }
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
}
