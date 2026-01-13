package se.tennis.tdouble.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matcher")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sasong_id")
    private Sasong sasong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupp_id")
    private Grupp grupp;

    @Column(name = "match_datum")
    private LocalDate matchDatum;

    @Column(name = "match_tid")
    private LocalTime matchTid;

    @Column
    private Integer bana;

    // Lag 1 - två spelare
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lag1_spelare1_id")
    private Spelare lag1Spelare1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lag1_spelare2_id")
    private Spelare lag1Spelare2;

    // Lag 2 - två spelare
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lag2_spelare1_id")
    private Spelare lag2Spelare1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lag2_spelare2_id")
    private Spelare lag2Spelare2;

    // Resultat - upp till 5 set
    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("setNummer ASC")
    @Builder.Default
    private List<SetResultat> setResultat = new ArrayList<>();

    @Column
    private boolean spelad = false;

    @Column
    private boolean walkover = false;

    // Beräknade fält
    @Column(name = "lag1_vunna_set")
    private Integer lag1VunnaSet = 0;

    @Column(name = "lag2_vunna_set")
    private Integer lag2VunnaSet = 0;

    public void addSetResultat(SetResultat resultat) {
        setResultat.add(resultat);
        resultat.setMatch(this);
    }

    public String getLag1Namn() {
        if (lag1Spelare1 == null || lag1Spelare2 == null) return "";
        return lag1Spelare1.getNamn() + "/" + lag1Spelare2.getNamn();
    }

    public String getLag2Namn() {
        if (lag2Spelare1 == null || lag2Spelare2 == null) return "";
        return lag2Spelare1.getNamn() + "/" + lag2Spelare2.getNamn();
    }

    public void beraknaVunnaSet() {
        int lag1 = 0;
        int lag2 = 0;
        for (SetResultat set : setResultat) {
            if (set.getLag1Gems() != null && set.getLag2Gems() != null) {
                if (set.getLag1Gems() > set.getLag2Gems()) {
                    lag1++;
                } else if (set.getLag2Gems() > set.getLag1Gems()) {
                    lag2++;
                }
            }
        }
        this.lag1VunnaSet = lag1;
        this.lag2VunnaSet = lag2;
    }
}
