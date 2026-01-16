package se.tennis.tdouble.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "spelare")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spelare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @Column
    private String email;

    @Column
    private String telefon;

    @Column
    private boolean aktiv = true;

    @ManyToMany(mappedBy = "spelare")
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Builder.Default
    private List<Grupp> grupper = new ArrayList<>();
}
