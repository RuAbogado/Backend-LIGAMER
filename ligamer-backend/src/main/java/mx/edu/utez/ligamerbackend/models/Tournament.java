package mx.edu.utez.ligamerbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tournaments")
@NoArgsConstructor
@Getter
@Setter
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String rules;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(columnDefinition = "BOOL DEFAULT TRUE")
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
