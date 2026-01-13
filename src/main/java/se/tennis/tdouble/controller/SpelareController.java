package se.tennis.tdouble.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.tennis.tdouble.dto.SpelareDTO;
import se.tennis.tdouble.entity.Spelare;
import se.tennis.tdouble.service.SpelareService;

import java.util.List;

@RestController
@RequestMapping("/api/spelare")
@RequiredArgsConstructor
public class SpelareController {

    private final SpelareService spelareService;

    @GetMapping
    public ResponseEntity<List<Spelare>> getAll() {
        return ResponseEntity.ok(spelareService.findAll());
    }

    @GetMapping("/aktiva")
    public ResponseEntity<List<Spelare>> getAktiva() {
        return ResponseEntity.ok(spelareService.findAktiva());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Spelare> getById(@PathVariable Long id) {
        return spelareService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Spelare>> search(@RequestParam String q) {
        return ResponseEntity.ok(spelareService.search(q));
    }

    @PostMapping
    public ResponseEntity<Spelare> create(@RequestBody SpelareDTO dto) {
        Spelare spelare = Spelare.builder()
                .namn(dto.getNamn())
                .email(dto.getEmail())
                .telefon(dto.getTelefon())
                .aktiv(true)
                .build();
        return ResponseEntity.ok(spelareService.save(spelare));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Spelare> update(@PathVariable Long id, @RequestBody SpelareDTO dto) {
        return spelareService.findById(id)
                .map(spelare -> {
                    spelare.setNamn(dto.getNamn());
                    spelare.setEmail(dto.getEmail());
                    spelare.setTelefon(dto.getTelefon());
                    return ResponseEntity.ok(spelareService.save(spelare));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        spelareService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
