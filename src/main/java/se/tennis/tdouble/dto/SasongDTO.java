package se.tennis.tdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SasongDTO {
    private Long id;
    private String namn;
    private LocalDate startDatum;
    private LocalDate slutDatum;
    private boolean aktiv;
    private se.tennis.tdouble.entity.ScoringSystem scoringSystem;
}
