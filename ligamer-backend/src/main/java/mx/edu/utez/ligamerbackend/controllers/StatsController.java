package mx.edu.utez.ligamerbackend.controllers;

import mx.edu.utez.ligamerbackend.dtos.PieDataDto;
import mx.edu.utez.ligamerbackend.dtos.RadarResponseDto;
import mx.edu.utez.ligamerbackend.dtos.TournamentSeriesDto;
import mx.edu.utez.ligamerbackend.dtos.ApiResponseDto;
import mx.edu.utez.ligamerbackend.services.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/pie")
    public ResponseEntity<ApiResponseDto<List<PieDataDto>>> getPie() {
        System.out.println("[StatsController] GET /api/stats/pie - entrada");
        try {
            List<PieDataDto> data = tournamentService.getPieStats();
            System.out.println("[StatsController] GET /api/stats/pie - datos obtenidos, size=" + (data != null ? data.size() : 0));
            return ResponseEntity.ok(ApiResponseDto.success("Datos pie obtenidos", data));
        } catch (Exception e) {
            System.out.println("[StatsController] GET /api/stats/pie - excepción: " + e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponseDto.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/radar")
    public ResponseEntity<ApiResponseDto<RadarResponseDto>> getRadar(@RequestParam(required = false) Long teamId,
                                                                      @RequestParam(required = false) Long tournamentId) {
        try {
            RadarResponseDto dto = tournamentService.getRadarStats(teamId, tournamentId);
            return ResponseEntity.ok(ApiResponseDto.success("Datos radar obtenidos", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponseDto.badRequest(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponseDto.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/series")
    public ResponseEntity<ApiResponseDto<List<TournamentSeriesDto>>> getSeries(@RequestParam(required = false) Long teamId) {
        try {
            List<TournamentSeriesDto> list = tournamentService.getTournamentSeries(teamId);
            return ResponseEntity.ok(ApiResponseDto.success("Series obtenidas", list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponseDto.error("Error: " + e.getMessage()));
        }
    }
}
