package se.tennis.tdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchDTO {
    private Long sasongId;
    private Long gruppId;
    private LocalDate matchDatum;
    private LocalTime matchTid;
    private Integer bana;
    private Long lag1Spelare1Id;
    private Long lag1Spelare2Id;
    private Long lag2Spelare1Id;
    private Long lag2Spelare2Id;
}
