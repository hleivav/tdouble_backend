package se.tennis.tdouble.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "set_resultat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetResultat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "set_nummer", nullable = false)
    private Integer setNummer;

    @Column(name = "lag1_gems")
    private Integer lag1Gems;

    @Column(name = "lag2_gems")
    private Integer lag2Gems;

    // För tiebreak
    @Column(name = "lag1_tiebreak")
    private Integer lag1Tiebreak;

    @Column(name = "lag2_tiebreak")
    private Integer lag2Tiebreak;
}
