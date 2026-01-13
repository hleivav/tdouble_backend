package se.tennis.tdouble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.tennis.tdouble.entity.Grupp;
import se.tennis.tdouble.entity.Sasong;
import se.tennis.tdouble.entity.Spelare;
import se.tennis.tdouble.repository.GruppRepository;
import se.tennis.tdouble.repository.SasongRepository;
import se.tennis.tdouble.repository.SpelareRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GruppService {

    private final GruppRepository gruppRepository;
    private final SasongRepository sasongRepository;
    private final SpelareRepository spelareRepository;

    public List<Grupp> findAll() {
        return gruppRepository.findAll();
    }

    public Optional<Grupp> findById(Long id) {
        return gruppRepository.findById(id);
    }

    public List<Grupp> findBySasongId(Long sasongId) {
        return gruppRepository.findBySasongIdOrderByGruppNummerAsc(sasongId);
    }

    public Grupp save(Grupp grupp) {
        return gruppRepository.save(grupp);
    }

    public Grupp create(Long sasongId, String namn, Integer gruppNummer) {
        Sasong sasong = sasongRepository.findById(sasongId)
                .orElseThrow(() -> new RuntimeException("Säsong hittades inte: " + sasongId));

        Grupp grupp = Grupp.builder()
                .namn(namn)
                .gruppNummer(gruppNummer)
                .sasong(sasong)
                .build();

        return gruppRepository.save(grupp);
    }

    public Grupp addSpelare(Long gruppId, Long spelareId) {
        Grupp grupp = gruppRepository.findById(gruppId)
                .orElseThrow(() -> new RuntimeException("Grupp hittades inte: " + gruppId));
        Spelare spelare = spelareRepository.findById(spelareId)
                .orElseThrow(() -> new RuntimeException("Spelare hittades inte: " + spelareId));

        grupp.addSpelare(spelare);
        return gruppRepository.save(grupp);
    }

    public Grupp removeSpelare(Long gruppId, Long spelareId) {
        Grupp grupp = gruppRepository.findById(gruppId)
                .orElseThrow(() -> new RuntimeException("Grupp hittades inte: " + gruppId));
        Spelare spelare = spelareRepository.findById(spelareId)
                .orElseThrow(() -> new RuntimeException("Spelare hittades inte: " + spelareId));

        grupp.removeSpelare(spelare);
        return gruppRepository.save(grupp);
    }

    public void delete(Long id) {
        gruppRepository.deleteById(id);
    }
}
