package mx.edu.utez.ligamerbackend.repositories;

import mx.edu.utez.ligamerbackend.models.Standing;
import mx.edu.utez.ligamerbackend.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StandingRepository extends JpaRepository<Standing, Long> {
    @Query("SELECT s FROM Standing s WHERE s.tournament = :tournament ORDER BY s.points DESC, (s.goalsFor - s.goalsAgainst) DESC, s.goalsFor DESC")
    List<Standing> findByTournamentOrderByPointsDescGoalDifferenceDescGoalsForDesc(Tournament tournament);
    
    Optional<Standing> findByTournamentAndTeamId(Tournament tournament, Long teamId);
}
