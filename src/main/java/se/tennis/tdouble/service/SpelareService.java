package se.tennis.tdouble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.tennis.tdouble.entity.Spelare;
import se.tennis.tdouble.repository.SpelareRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SpelareService {

    private final SpelareRepository spelareRepository;

    public List<Spelare> findAll() {
        return spelareRepository.findAll();
    }

    public List<Spelare> findAktiva() {
        return spelareRepository.findByAktivTrue();
    }

    public Optional<Spelare> findById(Long id) {
        return spelareRepository.findById(id);
    }

    public Optional<Spelare> findByNamn(String namn) {
        return spelareRepository.findByNamn(namn);
    }

    public List<Spelare> search(String query) {
        return spelareRepository.findByNamnContainingIgnoreCase(query);
    }

    public Spelare save(Spelare spelare) {
        return spelareRepository.save(spelare);
    }

    public Spelare create(String namn) {
        Spelare spelare = Spelare.builder()
                .namn(namn)
                .aktiv(true)
                .build();
        return spelareRepository.save(spelare);
    }

    public Spelare findOrCreate(String namn) {
        return spelareRepository.findByNamn(namn)
                .orElseGet(() -> create(namn));
    }

    public void delete(Long id) {
        spelareRepository.deleteById(id);
    }
}
