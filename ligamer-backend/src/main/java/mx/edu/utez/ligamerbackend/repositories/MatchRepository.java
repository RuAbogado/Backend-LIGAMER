package mx.edu.utez.ligamerbackend.repositories;

import mx.edu.utez.ligamerbackend.models.Match;
import mx.edu.utez.ligamerbackend.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentOrderByMatchDateAsc(Tournament tournament);
    List<Match> findByTournamentAndStatus(Tournament tournament, String status);
}
