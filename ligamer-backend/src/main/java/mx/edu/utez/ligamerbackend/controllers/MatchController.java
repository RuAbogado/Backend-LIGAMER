package mx.edu.utez.ligamerbackend.controllers;

import mx.edu.utez.ligamerbackend.dtos.ApiResponseDto;
import mx.edu.utez.ligamerbackend.dtos.ChallengeDto;
import mx.edu.utez.ligamerbackend.dtos.MatchDto;
import mx.edu.utez.ligamerbackend.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping("/challenge")
    public ResponseEntity<ApiResponseDto<MatchDto>> challengeTeam(@RequestBody ChallengeDto dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            MatchDto match = matchService.createChallenge(dto, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.success("Reto creado exitosamente", match));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Error al crear el reto: " + e.getMessage()));
        }
    }
}
