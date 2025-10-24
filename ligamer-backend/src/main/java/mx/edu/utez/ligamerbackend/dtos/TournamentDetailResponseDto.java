package mx.edu.utez.ligamerbackend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TournamentDetailResponseDto {
    private Long id;
    private String name;
    private String description;
    private String rules;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private String createdByEmail;
    private List<TeamSummaryDto> teams = new ArrayList<>();
}
