package se.tennis.tdouble.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.tennis.tdouble.dto.MatchDTO;
import se.tennis.tdouble.dto.ResultatDTO;
import se.tennis.tdouble.entity.Match;
import se.tennis.tdouble.service.MatchService;
import se.tennis.tdouble.service.SasongService;
import se.tennis.tdouble.service.GruppService;
import se.tennis.tdouble.service.SpelareService;

import java.util.List;

@RestController
@RequestMapping("/api/matcher")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final SasongService sasongService;
    private final GruppService gruppService;
    private final SpelareService spelareService;

    @GetMapping
    public ResponseEntity<List<Match>> getAll() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getById(@PathVariable Long id) {
        return matchService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sasong/{sasongId}")
    public ResponseEntity<List<Match>> getBySasong(@PathVariable Long sasongId) {
        return ResponseEntity.ok(matchService.findBySasongId(sasongId));
    }

    @GetMapping("/grupp/{gruppId}")
    public ResponseEntity<List<Match>> getByGrupp(@PathVariable Long gruppId) {
        return ResponseEntity.ok(matchService.findByGruppId(gruppId));
    }

    @GetMapping("/sasong/{sasongId}/ospelade")
    public ResponseEntity<List<Match>> getOspelade(@PathVariable Long sasongId) {
        return ResponseEntity.ok(matchService.findOspelade(sasongId));
    }

    @GetMapping("/sasong/{sasongId}/spelade")
    public ResponseEntity<List<Match>> getSpelade(@PathVariable Long sasongId) {
        return ResponseEntity.ok(matchService.findSpelade(sasongId));
    }

    @PostMapping
    public ResponseEntity<Match> create(@RequestBody MatchDTO dto) {
        Match match = new Match();

        if (dto.getSasongId() != null) {
            sasongService.findById(dto.getSasongId()).ifPresent(match::setSasong);
        }
        if (dto.getGruppId() != null) {
            gruppService.findById(dto.getGruppId()).ifPresent(match::setGrupp);
        }
        match.setMatchDatum(dto.getMatchDatum());
        match.setMatchTid(dto.getMatchTid());
        match.setBana(dto.getBana());

        if (dto.getLag1Spelare1Id() != null) spelareService.findById(dto.getLag1Spelare1Id()).ifPresent(match::setLag1Spelare1);
        if (dto.getLag1Spelare2Id() != null) spelareService.findById(dto.getLag1Spelare2Id()).ifPresent(match::setLag1Spelare2);
        if (dto.getLag2Spelare1Id() != null) spelareService.findById(dto.getLag2Spelare1Id()).ifPresent(match::setLag2Spelare1);
        if (dto.getLag2Spelare2Id() != null) spelareService.findById(dto.getLag2Spelare2Id()).ifPresent(match::setLag2Spelare2);

        Match saved = matchService.save(match);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/resultat")
    public ResponseEntity<Match> rapporteraResultat(@PathVariable Long id, @RequestBody ResultatDTO dto) {
        return ResponseEntity.ok(matchService.rapporteraResultat(id, dto.getSetResultat()));
    }

    @PostMapping("/{id}/walkover")
    public ResponseEntity<Match> setWalkover(@PathVariable Long id, @RequestParam int vinnare) {
        return ResponseEntity.ok(matchService.setWalkover(id, vinnare));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
