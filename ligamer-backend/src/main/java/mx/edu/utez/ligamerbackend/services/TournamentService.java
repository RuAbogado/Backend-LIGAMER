package mx.edu.utez.ligamerbackend.services;

import mx.edu.utez.ligamerbackend.dtos.TournamentDto;
import mx.edu.utez.ligamerbackend.dtos.TournamentResponseDto;
import mx.edu.utez.ligamerbackend.models.Tournament;
import mx.edu.utez.ligamerbackend.models.User;
import mx.edu.utez.ligamerbackend.repositories.TournamentRepository;
import mx.edu.utez.ligamerbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    public TournamentResponseDto createTournament(TournamentDto dto, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));

        Tournament t = new Tournament();
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setRules(dto.getRules());
        t.setStartDate(dto.getStartDate());
        t.setEndDate(dto.getEndDate());
        t.setActive(true);
        t.setCreatedBy(creator);

        Tournament saved = tournamentRepository.save(t);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponseDto> listAll() {
        return tournamentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private TournamentResponseDto toDto(Tournament t) {
        TournamentResponseDto r = new TournamentResponseDto();
        r.setId(t.getId());
        r.setName(t.getName());
        r.setDescription(t.getDescription());
        r.setRules(t.getRules());
        r.setStartDate(t.getStartDate());
        r.setEndDate(t.getEndDate());
        r.setActive(t.isActive());
        r.setCreatedByEmail(t.getCreatedBy() != null ? t.getCreatedBy().getEmail() : null);
        return r;
    }
}
