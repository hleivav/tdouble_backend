package se.tennis.tdouble.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grupper")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grupp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @Column(name = "grupp_nummer")
    private Integer gruppNummer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sasong_id")
    private Sasong sasong;

    @ManyToMany
    @JoinTable(
        name = "grupp_spelare",
        joinColumns = @JoinColumn(name = "grupp_id"),
        inverseJoinColumns = @JoinColumn(name = "spelare_id")
    )
    @Builder.Default
    private List<Spelare> spelare = new ArrayList<>();

    public void addSpelare(Spelare spelare) {
        this.spelare.add(spelare);
    }

    public void removeSpelare(Spelare spelare) {
        this.spelare.remove(spelare);
    }
}
