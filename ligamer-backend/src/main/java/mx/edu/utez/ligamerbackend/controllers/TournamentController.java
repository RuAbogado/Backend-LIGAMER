package mx.edu.utez.ligamerbackend.controllers;

import mx.edu.utez.ligamerbackend.dtos.TournamentDto;
import mx.edu.utez.ligamerbackend.dtos.TournamentResponseDto;
import mx.edu.utez.ligamerbackend.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import mx.edu.utez.ligamerbackend.utils.AppConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping
    @PreAuthorize("hasAuthority('" + AppConstants.ROLE_ORGANIZADOR + "') or hasAuthority('" + AppConstants.ROLE_ADMINISTRADOR + "')")
    public ResponseEntity<TournamentResponseDto> create(@RequestBody TournamentDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        TournamentResponseDto created = tournamentService.createTournament(dto, email);
        return ResponseEntity.created(URI.create("/api/tournaments/" + created.getId())).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponseDto>> listAll() {
        return ResponseEntity.ok(tournamentService.listAll());
    }
}
