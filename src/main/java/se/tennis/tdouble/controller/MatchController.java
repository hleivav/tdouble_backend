package se.tennis.tdouble.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.tennis.tdouble.dto.MatchDTO;
import se.tennis.tdouble.dto.ResultatDTO;
import se.tennis.tdouble.entity.Match;
import se.tennis.tdouble.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matcher")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

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
        // TODO: Implementera matchskapande med spelar-IDs
        return ResponseEntity.ok(matchService.save(new Match()));
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
