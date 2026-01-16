package se.tennis.tdouble.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GruppDTO {
    @NotNull
    private Long sasongId;

    @NotBlank
    private String namn;

    private Integer gruppNummer;
}
