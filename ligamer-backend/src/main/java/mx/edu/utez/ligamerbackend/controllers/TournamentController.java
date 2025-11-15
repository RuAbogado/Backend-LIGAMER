package mx.edu.utez.ligamerbackend.controllers;

import mx.edu.utez.ligamerbackend.dtos.TournamentDto;
import mx.edu.utez.ligamerbackend.dtos.TournamentResponseDto;
import mx.edu.utez.ligamerbackend.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import mx.edu.utez.ligamerbackend.dtos.TournamentDetailResponseDto;
import mx.edu.utez.ligamerbackend.dtos.StandingDto;
import mx.edu.utez.ligamerbackend.dtos.MatchDto;
import mx.edu.utez.ligamerbackend.dtos.MatchResultDto;
import mx.edu.utez.ligamerbackend.dtos.ApiResponseDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // Endpoint de prueba para verificar autenticación
    @GetMapping("/test-auth")
    public ResponseEntity<ApiResponseDto<String>> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String authorities = auth.getAuthorities().toString();
        String message = "Usuario: " + email + " | Authorities: " + authorities;
        System.out.println("🧪 TEST AUTH: " + message);
        return ResponseEntity.ok(ApiResponseDto.success("Test de autenticación exitoso", message));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZADOR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<TournamentResponseDto>> create(@RequestBody TournamentDto dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            TournamentResponseDto created = tournamentService.createTournament(dto, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("Torneo creado exitosamente", created));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al crear el torneo: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<TournamentResponseDto>>> listAll() {
        try {
            List<TournamentResponseDto> tournaments = tournamentService.listAll();
            return ResponseEntity.ok(ApiResponseDto.success("Torneos obtenidos exitosamente", tournaments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al obtener los torneos: " + e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<ApiResponseDto<TournamentDetailResponseDto>> getTournament(@PathVariable Long tournamentId) {
        try {
            TournamentDetailResponseDto tournament = tournamentService.getTournament(tournamentId);
            return ResponseEntity.ok(ApiResponseDto.success("Detalle del torneo obtenido", tournament));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al obtener el torneo: " + e.getMessage()));
        }
    }

    @PutMapping("/{tournamentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZADOR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<TournamentResponseDto>> update(@PathVariable Long tournamentId, @RequestBody TournamentDto dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            TournamentResponseDto updated = tournamentService.updateTournament(tournamentId, dto, email);
            return ResponseEntity.ok(ApiResponseDto.success("Torneo actualizado exitosamente", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al actualizar el torneo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{tournamentId}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long tournamentId) {
        try {
            tournamentService.deleteTournament(tournamentId);
            return ResponseEntity.ok(ApiResponseDto.success("Torneo eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al eliminar el torneo: " + e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/standings")
    public ResponseEntity<ApiResponseDto<List<StandingDto>>> getStandings(@PathVariable Long tournamentId) {
        try {
            List<StandingDto> standings = tournamentService.getStandings(tournamentId);
            return ResponseEntity.ok(ApiResponseDto.success("Tabla de posiciones obtenida", standings));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al obtener la tabla de posiciones: " + e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/matches")
    public ResponseEntity<ApiResponseDto<List<MatchDto>>> getMatches(@PathVariable Long tournamentId) {
        try {
            List<MatchDto> matches = tournamentService.getMatches(tournamentId);
            return ResponseEntity.ok(ApiResponseDto.success("Calendario de partidos obtenido", matches));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al obtener el calendario: " + e.getMessage()));
        }
    }

    @PostMapping("/{tournamentId}/teams")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> enrollTeam(
            @PathVariable Long tournamentId,
            @RequestParam Long teamId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            tournamentService.enrollTeam(tournamentId, teamId, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("Equipo inscrito al torneo exitosamente"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.notFound(e.getMessage()));
            } else if (e.getMessage().contains("ya está inscrito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.conflict(e.getMessage()));
            } else if (e.getMessage().contains("autorizado")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.unauthorized(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.badRequest(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al inscribir el equipo: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{tournamentId}/teams/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> removeTeam(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            tournamentService.removeTeam(tournamentId, teamId, email);
            return ResponseEntity.ok(ApiResponseDto.success("Equipo retirado del torneo exitosamente"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.notFound(e.getMessage()));
            } else if (e.getMessage().contains("no está inscrito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.conflict(e.getMessage()));
            } else if (e.getMessage().contains("autorizado")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.unauthorized(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.badRequest(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al retirar el equipo: " + e.getMessage()));
        }
    }

    @PostMapping("/matches/{matchId}/result")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZADOR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<MatchDto>> registerMatchResult(
            @PathVariable Long matchId,
            @RequestBody MatchResultDto resultDto) {
        System.out.println("🎯 ENTRANDO a registerMatchResult - matchId: " + matchId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔑 Authentication: " + auth);
        System.out.println("👤 Principal: " + (auth != null ? auth.getName() : "NULL"));
        System.out.println("🛡️ Authorities: " + (auth != null ? auth.getAuthorities() : "NULL"));
        
        try {
            String email = auth.getName();
            MatchDto match = tournamentService.registerMatchResult(matchId, resultDto, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("Resultado del partido registrado exitosamente", match));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.notFound(e.getMessage()));
            } else if (e.getMessage().contains("ya tiene un resultado")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.conflict(e.getMessage()));
            } else if (e.getMessage().contains("autorizado")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.unauthorized(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.badRequest(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al registrar el resultado: " + e.getMessage()));
        }
    }

    @PutMapping("/matches/{matchId}/result")
    @PreAuthorize("hasAnyAuthority('ROLE_ORGANIZADOR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ApiResponseDto<MatchDto>> updateMatchResult(
            @PathVariable Long matchId,
            @RequestBody MatchResultDto resultDto) {
        System.out.println("🔄 ENTRANDO a updateMatchResult - matchId: " + matchId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔑 Authentication: " + auth);
        System.out.println("👤 Principal: " + (auth != null ? auth.getName() : "NULL"));
        System.out.println("🛡️ Authorities: " + (auth != null ? auth.getAuthorities() : "NULL"));
        
        try {
            String email = auth.getName();
            MatchDto match = tournamentService.updateMatchResult(matchId, resultDto, email);
            return ResponseEntity.ok(ApiResponseDto.success("Resultado del partido actualizado exitosamente", match));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDto.notFound(e.getMessage()));
            } else if (e.getMessage().contains("no tiene un resultado") || e.getMessage().contains("Use el endpoint POST")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponseDto.conflict(e.getMessage()));
            } else if (e.getMessage().contains("autorizado")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDto.unauthorized(e.getMessage()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDto.badRequest(e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al actualizar el resultado: " + e.getMessage()));
        }
    }
}

