package se.tennis.tdouble.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.tennis.tdouble.dto.SasongDTO;
import se.tennis.tdouble.entity.Sasong;
import se.tennis.tdouble.service.SasongService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sasonger")
@RequiredArgsConstructor
public class SasongController {

    private final SasongService sasongService;

    private SasongDTO toDTO(Sasong sasong) {
        SasongDTO dto = new SasongDTO();
        dto.setId(sasong.getId());
        dto.setNamn(sasong.getNamn());
        dto.setStartDatum(sasong.getStartDatum());
        dto.setSlutDatum(sasong.getSlutDatum());
        dto.setAktiv(sasong.isAktiv());
        dto.setScoringSystem(sasong.getScoringSystem());
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<SasongDTO>> getAll() {
        List<SasongDTO> dtos = sasongService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SasongDTO> getById(@PathVariable Long id) {
        return sasongService.findById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/aktiva")
    public ResponseEntity<List<SasongDTO>> getAktiva() {
        List<SasongDTO> dtos = sasongService.findAktiva().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/avslutade")
    public ResponseEntity<List<SasongDTO>> getAvslutade() {
        List<SasongDTO> dtos = sasongService.findAvslutade().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<SasongDTO> create(@RequestBody SasongDTO dto) {
        Sasong sasong = Sasong.builder()
                .namn(dto.getNamn())
                .startDatum(dto.getStartDatum())
                .slutDatum(dto.getSlutDatum())
                .aktiv(true)
                .scoringSystem(dto.getScoringSystem() == null ? se.tennis.tdouble.entity.ScoringSystem.GAME_BASED : dto.getScoringSystem())
                .build();
        return ResponseEntity.ok(toDTO(sasongService.save(sasong)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SasongDTO> update(@PathVariable Long id, @RequestBody SasongDTO dto) {
        return sasongService.findById(id)
                .map(sasong -> {
                    sasong.setNamn(dto.getNamn());
                    sasong.setStartDatum(dto.getStartDatum());
                    sasong.setSlutDatum(dto.getSlutDatum());
                    sasong.setScoringSystem(dto.getScoringSystem());
                    return ResponseEntity.ok(toDTO(sasongService.save(sasong)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/avsluta")
    public ResponseEntity<SasongDTO> avsluta(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(sasongService.avslutaSasong(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sasongService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
