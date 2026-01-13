package se.tennis.tdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GruppDTO {
    private Long sasongId;
    private String namn;
    private Integer gruppNummer;
}
