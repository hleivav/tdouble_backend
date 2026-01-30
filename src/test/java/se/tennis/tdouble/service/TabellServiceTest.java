package se.tennis.tdouble.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.tennis.tdouble.entity.*;
import se.tennis.tdouble.repository.GruppRepository;
import se.tennis.tdouble.repository.MatchRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TabellServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private GruppRepository gruppRepository;

    @Mock
    private SasongService sasongService;

    @InjectMocks
    private TabellService tabellService;

    private Match createMatchWithSets(Sasong sasong, String lag1Name, String lag2Name, int[][] sets, boolean walkover, int walkoverWinner) {
        Match m = Match.builder().build();
        m.setSasong(sasong);
        m.setSpelad(true);
        m.setWalkover(walkover);

        Spelare a1 = Spelare.builder().namn(lag1Name + "_A").build();
        Spelare a2 = Spelare.builder().namn(lag1Name + "_B").build();
        Spelare b1 = Spelare.builder().namn(lag2Name + "_A").build();
        Spelare b2 = Spelare.builder().namn(lag2Name + "_B").build();
        m.setLag1Spelare1(a1); m.setLag1Spelare2(a2);
        m.setLag2Spelare1(b1); m.setLag2Spelare2(b2);

        for (int i = 0; i < sets.length; i++) {
            int[] s = sets[i];
            if (s[0] >= 0 && s[1] >= 0) {
                SetResultat sr = SetResultat.builder()
                        .setNummer(i + 1)
                        .lag1Gems(s[0])
                        .lag2Gems(s[1])
                        .build();
                m.addSetResultat(sr);
            }
        }

        if (walkover) {
            if (walkoverWinner == 1) {
                m.setLag1VunnaSet(2); m.setLag2VunnaSet(0);
            } else {
                m.setLag1VunnaSet(0); m.setLag2VunnaSet(2);
            }
        } else {
            m.beraknaVunnaSet();
        }

        return m;
    }

    @Test
    void gameBasedInterruptedMatch() {
        Sasong s = Sasong.builder().scoringSystem(ScoringSystem.GAME_BASED).build();
        Grupp g = Grupp.builder().build(); g.setSasong(s);

        // 6-4, 3-6, 4-3 (avbruten)
        Match m = createMatchWithSets(s, "A", "B", new int[][]{{6,4},{3,6},{4,3}}, false, 0);

        when(gruppRepository.findById(1L)).thenReturn(Optional.of(g));
        when(matchRepository.findSpeladMatcherByGrupp(1L)).thenReturn(List.of(m));

        var res = tabellService.beraknaTabell(1L);
        // Both should have 15 points (13 games + 2 set bonus)
        assertEquals(15.0, res.get(0).getPoang(), 0.0001);
        assertEquals(15.0, res.get(1).getPoang(), 0.0001);
    }

    @Test
    void setBasedInterruptedMatch() {
        Sasong s = Sasong.builder().scoringSystem(ScoringSystem.SET_BASED).build();
        Grupp g = Grupp.builder().build(); g.setSasong(s);

        Match m = createMatchWithSets(s, "A", "B", new int[][]{{6,4},{3,6},{4,3}}, false, 0);

        when(gruppRepository.findById(1L)).thenReturn(Optional.of(g));
        when(matchRepository.findSpeladMatcherByGrupp(1L)).thenReturn(List.of(m));

        var res = tabellService.beraknaTabell(1L);
        // 1 + 1 + 0.5 each = 1.5 each
        assertEquals(1.5, res.get(0).getPoang(), 0.0001);
        assertEquals(1.5, res.get(1).getPoang(), 0.0001);
    }

    @Test
    void momentumInterruptedMatch() {
        Sasong s = Sasong.builder().scoringSystem(ScoringSystem.MOMENTUM).build();
        Grupp g = Grupp.builder().build(); g.setSasong(s);

        Match m = createMatchWithSets(s, "A", "B", new int[][]{{6,4},{3,6},{4,3}}, false, 0);

        when(gruppRepository.findById(1L)).thenReturn(Optional.of(g));
        when(matchRepository.findSpeladMatcherByGrupp(1L)).thenReturn(List.of(m));

        var res = tabellService.beraknaTabell(1L);
        // A: set1 (2) + incomplete leading (1) = 3; B: set2 (2)
        assertEquals(3.0, res.get(0).getPoang(), 0.0001);
        assertEquals(2.0, res.get(1).getPoang(), 0.0001);
    }

    @Test
    void walkover() {
        Sasong s1 = Sasong.builder().scoringSystem(ScoringSystem.GAME_BASED).build();
        Grupp g1 = Grupp.builder().build(); g1.setSasong(s1);
        Match mw = createMatchWithSets(s1, "A", "B", new int[][]{}, true, 1);

        when(gruppRepository.findById(1L)).thenReturn(Optional.of(g1));
        when(matchRepository.findSpeladMatcherByGrupp(1L)).thenReturn(List.of(mw));

        var res = tabellService.beraknaTabell(1L);
        assertEquals(4.0, res.get(0).getPoang(), 0.0001);
        assertEquals(0.0, res.get(1).getPoang(), 0.0001);
    }

    @Test
    void setBasedFallbackWhenMatchHasNoSasong() {
        // Active season is SET_BASED, but match has no sasong set
        Sasong active = Sasong.builder().scoringSystem(ScoringSystem.SET_BASED).build();
        Grupp g = Grupp.builder().build(); g.setSasong(active);

        // Create match without associating sasong (null)
        Match m = createMatchWithSets(null, "A", "B", new int[][]{{6,4},{4,6},{6,2}}, false, 0);

        when(gruppRepository.findById(1L)).thenReturn(Optional.of(g));
        when(matchRepository.findSpeladMatcherByGrupp(1L)).thenReturn(List.of(m));
        when(sasongService.findAktiva()).thenReturn(List.of(active));

        var res = tabellService.beraknaTabell(1L);
        // SET_BASED: A wins 2 sets, B wins 1 => A=2, B=1
        assertEquals(2.0, res.get(0).getPoang(), 0.0001);
        assertEquals(1.0, res.get(1).getPoang(), 0.0001);
    }
}
