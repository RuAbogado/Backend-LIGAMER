package mx.edu.utez.ligamerbackend.repositories;

import mx.edu.utez.ligamerbackend.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
