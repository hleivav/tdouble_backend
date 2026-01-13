package se.tennis.tdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultatDTO {
    // Lista med set-resultat, varje int[] är [lag1Gems, lag2Gems]
    private List<int[]> setResultat;
}
