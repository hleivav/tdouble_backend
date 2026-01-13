package se.tennis.tdouble.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.tennis.tdouble.dto.GruppDTO;
import se.tennis.tdouble.entity.Grupp;
import se.tennis.tdouble.service.GruppService;

import java.util.List;

@RestController
@RequestMapping("/api/grupper")
@RequiredArgsConstructor
public class GruppController {

    private final GruppService gruppService;

    @GetMapping
    public ResponseEntity<List<Grupp>> getAll() {
        return ResponseEntity.ok(gruppService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grupp> getById(@PathVariable Long id) {
        return gruppService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sasong/{sasongId}")
    public ResponseEntity<List<Grupp>> getBySasong(@PathVariable Long sasongId) {
        return ResponseEntity.ok(gruppService.findBySasongId(sasongId));
    }

    @PostMapping
    public ResponseEntity<Grupp> create(@RequestBody GruppDTO dto) {
        return ResponseEntity.ok(gruppService.create(dto.getSasongId(), dto.getNamn(), dto.getGruppNummer()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grupp> update(@PathVariable Long id, @RequestBody GruppDTO dto) {
        return gruppService.findById(id)
                .map(grupp -> {
                    grupp.setNamn(dto.getNamn());
                    grupp.setGruppNummer(dto.getGruppNummer());
                    return ResponseEntity.ok(gruppService.save(grupp));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{gruppId}/spelare/{spelareId}")
    public ResponseEntity<Grupp> addSpelare(@PathVariable Long gruppId, @PathVariable Long spelareId) {
        return ResponseEntity.ok(gruppService.addSpelare(gruppId, spelareId));
    }

    @DeleteMapping("/{gruppId}/spelare/{spelareId}")
    public ResponseEntity<Grupp> removeSpelare(@PathVariable Long gruppId, @PathVariable Long spelareId) {
        return ResponseEntity.ok(gruppService.removeSpelare(gruppId, spelareId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gruppService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
