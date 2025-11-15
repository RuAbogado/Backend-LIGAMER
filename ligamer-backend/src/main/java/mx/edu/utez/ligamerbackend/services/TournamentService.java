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
import mx.edu.utez.ligamerbackend.dtos.StandingDto;
import mx.edu.utez.ligamerbackend.dtos.MatchDto;
import mx.edu.utez.ligamerbackend.models.Standing;
import mx.edu.utez.ligamerbackend.models.Match;
import mx.edu.utez.ligamerbackend.repositories.StandingRepository;
import mx.edu.utez.ligamerbackend.repositories.MatchRepository;

@Service
@Transactional
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StandingRepository standingRepository;

    @Autowired
    private MatchRepository matchRepository;

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


    public void deleteTournament(Long tournamentId) throws Exception {
        Tournament found = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado."));
        tournamentRepository.delete(found);
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

    @Transactional(readOnly = true)
    public List<StandingDto> getStandings(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado."));

        List<Standing> standings = standingRepository.findByTournamentOrderByPointsDescGoalDifferenceDescGoalsForDesc(tournament);
        
        List<StandingDto> result = new java.util.ArrayList<>();
        int position = 1;
        for (Standing standing : standings) {
            StandingDto dto = new StandingDto(
                    standing.getId(),
                    standing.getTeam().getName(),
                    standing.getTeam().getId(),
                    standing.getPlayed(),
                    standing.getWon(),
                    standing.getDrawn(),
                    standing.getLost(),
                    standing.getGoalsFor(),
                    standing.getGoalsAgainst(),
                    standing.getPoints()
            );
            dto.setPosition(position++);
            result.add(dto);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<MatchDto> getMatches(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado."));

        List<Match> matches = matchRepository.findByTournamentOrderByMatchDateAsc(tournament);
        
        return matches.stream().map(match -> {
            MatchDto dto = new MatchDto();
            dto.setId(match.getId());
            dto.setHomeTeamName(match.getHomeTeam().getName());
            dto.setHomeTeamId(match.getHomeTeam().getId());
            dto.setAwayTeamName(match.getAwayTeam().getName());
            dto.setAwayTeamId(match.getAwayTeam().getId());
            dto.setHomeScore(match.getHomeScore());
            dto.setAwayScore(match.getAwayScore());
            dto.setMatchDate(match.getMatchDate());
            dto.setStatus(match.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    public void enrollTeam(Long tournamentId, Long teamId, String requesterEmail) throws Exception {
        // Verificar que el torneo existe
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado."));

        // Verificar que el equipo existe
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado."));

        // Verificar que el solicitante es el dueño del equipo
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (!team.getOwner().getId().equals(requester.getId())) {
            throw new RuntimeException("No estás autorizado. Solo el dueño del equipo puede inscribirse.");
        }

        // Verificar que el equipo no está ya inscrito
        if (tournament.getTeams().contains(team)) {
            throw new RuntimeException("El equipo ya está inscrito en este torneo.");
        }

        // Inscribir el equipo
        tournament.getTeams().add(team);
        team.getTournaments().add(tournament);
        tournamentRepository.save(tournament);
    }

    public void removeTeam(Long tournamentId, Long teamId, String requesterEmail) throws Exception {
        // Verificar que el torneo existe
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado."));

        // Verificar que el equipo existe
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado."));

        // Verificar que el equipo está inscrito en el torneo
        if (!tournament.getTeams().contains(team)) {
            throw new RuntimeException("El equipo no está inscrito en este torneo.");
        }

        // Obtener datos del solicitante
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Verificar autorización: dueño del equipo u organizador/admin
        boolean isTeamOwner = team.getOwner().getId().equals(requester.getId());
        String roleName = requester.getRole() != null ? requester.getRole().getName() : null;
        boolean isOrganizerOrAdmin = AppConstants.ROLE_ORGANIZADOR.equals(roleName) || 
                                     AppConstants.ROLE_ADMINISTRADOR.equals(roleName);

        if (!isTeamOwner && !isOrganizerOrAdmin) {
            throw new RuntimeException("No estás autorizado para retirar este equipo del torneo.");
        }

        // Retirar el equipo
        tournament.getTeams().remove(team);
        team.getTournaments().remove(tournament);
        tournamentRepository.save(tournament);
    }
}