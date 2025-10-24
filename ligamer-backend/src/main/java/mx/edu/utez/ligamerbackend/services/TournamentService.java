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
import mx.edu.utez.ligamerbackend.dtos.TeamSummaryDto;
import mx.edu.utez.ligamerbackend.dtos.TournamentDetailResponseDto;
import mx.edu.utez.ligamerbackend.models.Team;
import mx.edu.utez.ligamerbackend.repositories.TeamRepository;
import mx.edu.utez.ligamerbackend.utils.AppConstants;

@Service
@Transactional
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

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
    public TournamentDetailResponseDto getTournament(Long tournamentId) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado."));

        TournamentDetailResponseDto dto = new TournamentDetailResponseDto();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        dto.setRules(t.getRules());
        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());
        dto.setActive(t.isActive());
        dto.setCreatedByEmail(t.getCreatedBy() != null ? t.getCreatedBy().getEmail() : null);

        // Actualmente no hay relación directa entre Tournament y Team en el modelo.
        // Como aproximación segura devolvemos la lista vacía. Si se modela la relación,
        // aquí se podrá mapear los equipos inscritos.
        List<Team> teams = List.of();
        List<TeamSummaryDto> teamDtos = teams.stream().map(team -> {
            TeamSummaryDto ts = new TeamSummaryDto();
            ts.setId(team.getId());
            ts.setName(team.getName());
            ts.setOwnerEmail(team.getOwner() != null ? team.getOwner().getEmail() : null);
            return ts;
        }).collect(Collectors.toList());
        dto.setTeams(teamDtos);

        return dto;
    }

    public TournamentResponseDto updateTournament(Long tournamentId, TournamentDto dto, String requesterEmail) throws Exception {
        // Verificar rol del solicitante
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Usuario solicitante no encontrado."));

        String roleName = requester.getRole() != null ? requester.getRole().getName() : null;
        boolean allowed = AppConstants.ROLE_ORGANIZADOR.equals(roleName) || AppConstants.ROLE_ADMINISTRADOR.equals(roleName);
        if (!allowed) throw new Exception("No autorizado.");

        Tournament found = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado."));

        if (dto.getName() != null) found.setName(dto.getName());
        if (dto.getDescription() != null) found.setDescription(dto.getDescription());
        if (dto.getRules() != null) found.setRules(dto.getRules());
        if (dto.getStartDate() != null) found.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) found.setEndDate(dto.getEndDate());

        Tournament saved = tournamentRepository.save(found);
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
