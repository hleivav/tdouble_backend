package se.tennis.tdouble.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.tennis.tdouble.dto.TabellRadDTO;
import se.tennis.tdouble.service.TabellService;

import java.util.List;

@RestController
@RequestMapping("/api/tabell")
@RequiredArgsConstructor
public class TabellController {

    private final TabellService tabellService;

    @GetMapping("/grupp/{gruppId}")
    public ResponseEntity<List<TabellRadDTO>> getTabellForGrupp(@PathVariable Long gruppId) {
        return ResponseEntity.ok(tabellService.beraknaTabell(gruppId));
    }
}
