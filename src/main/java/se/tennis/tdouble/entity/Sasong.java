package se.tennis.tdouble.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sasonger")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sasong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @Column(name = "start_datum")
    private LocalDate startDatum;

    @Column(name = "slut_datum")
    private LocalDate slutDatum;

    @Column(nullable = false)
    private boolean aktiv = true;


    @OneToMany(mappedBy = "sasong", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Grupp> grupper = new ArrayList<>();


    @OneToMany(mappedBy = "sasong", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Match> matcher = new ArrayList<>();

    public void addGrupp(Grupp grupp) {
        grupper.add(grupp);
        grupp.setSasong(this);
    }

    public void removeGrupp(Grupp grupp) {
        grupper.remove(grupp);
        grupp.setSasong(null);
    }
}
