package se.tennis.tdouble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TabellRadDTO {
    private String lagNamn;
    private int placering;
    private int spelade;
    private int vinster;
    private int oavgjorda;
    private int forluster;
    private int vunnaSet;
    private int forloradeSet;
    private int vunnaGems;
    private int forloradeGems;
    private int setDifferens;
    private int gemsDifferens;
    private double poang;

    public TabellRadDTO(String lagNamn) {
        this.lagNamn = lagNamn;
        this.spelade = 0;
        this.vinster = 0;
        this.oavgjorda = 0;
        this.forluster = 0;
        this.vunnaSet = 0;
        this.forloradeSet = 0;
        this.vunnaGems = 0;
        this.forloradeGems = 0;
        this.poang = 0.0;
    }

    public void addPoang(double p) {
        this.poang += p;
    }

    public void beraknaPoangOchDifferens() {
        this.setDifferens = vunnaSet - forloradeSet;
        this.gemsDifferens = vunnaGems - forloradeGems;
    }
}
