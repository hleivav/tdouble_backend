package se.tennis.tdouble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.tennis.tdouble.entity.Sasong;
import se.tennis.tdouble.repository.SasongRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SasongService {

    private final SasongRepository sasongRepository;

    public List<Sasong> findAll() {
        return sasongRepository.findAllByOrderByStartDatumDesc();
    }

    public Optional<Sasong> findById(Long id) {
        return sasongRepository.findById(id);
    }

    public List<Sasong> findAktiva() {
        return sasongRepository.findByAktivTrue();
    }

    public List<Sasong> findAvslutade() {
        return sasongRepository.findByAktivFalse();
    }

    public Sasong save(Sasong sasong) {
        return sasongRepository.save(sasong);
    }

    public Sasong create(String namn) {
        Sasong sasong = Sasong.builder()
                .namn(namn)
                .aktiv(true)
                .build();
        return sasongRepository.save(sasong);
    }

    public Sasong avslutaSasong(Long id) {
        Sasong sasong = sasongRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Säsong hittades inte: " + id));
        sasong.setAktiv(false);
        return sasongRepository.save(sasong);
    }

    public void delete(Long id) {
        sasongRepository.deleteById(id);
    }
}
