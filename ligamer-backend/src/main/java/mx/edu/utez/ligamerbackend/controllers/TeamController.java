package mx.edu.utez.ligamerbackend.controllers;

import mx.edu.utez.ligamerbackend.dtos.ActionDto;
import mx.edu.utez.ligamerbackend.dtos.TeamDto;
import mx.edu.utez.ligamerbackend.models.JoinRequest;
import mx.edu.utez.ligamerbackend.models.Team;
import mx.edu.utez.ligamerbackend.models.User;
import mx.edu.utez.ligamerbackend.services.TeamService;
import mx.edu.utez.ligamerbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamDto teamDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Team team = teamService.createTeam(email, teamDto);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", team.getId());
            resp.put("message", "Equipo creado");
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listTeams() {
        System.out.println("🏆 ENTRANDO a listTeams");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔑 Authentication: " + authentication);
        System.out.println("👤 Principal: " + (authentication != null ? authentication.getName() : "NULL"));
        System.out.println("🛡️ Authorities: " + (authentication != null ? authentication.getAuthorities() : "NULL"));

        List<Team> teams = teamService.listTeams();
        System.out.println("📋 Equipos encontrados: " + teams.size());
        List<Map<String, Object>> response = teams.stream()
                .filter(t -> t.getOwner() != null) // Filtrar equipos sin propietario
                .map(t -> {
                    Map<String, Object> owner = new HashMap<>();
                    owner.put("id", t.getOwner().getId());
                    owner.put("email", t.getOwner().getEmail());
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", t.getId());
                    m.put("name", t.getName());
                    m.put("description", t.getDescription());
                    m.put("logoUrl", t.getLogoUrl());
                    m.put("owner", owner);
                    return m;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeam(@PathVariable Long teamId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Team team = teamService.getTeam(teamId);
            boolean isMember = team.getMembers() != null
                    && team.getMembers().stream().anyMatch(u -> u.getEmail().equals(email));
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", team.getId());
            resp.put("name", team.getName());
            resp.put("description", team.getDescription());
            resp.put("logoUrl", team.getLogoUrl());
            Map<String, Object> owner = new HashMap<>();
            owner.put("id", team.getOwner().getId());
            owner.put("email", team.getOwner().getEmail());
            resp.put("owner", owner);
            if (isMember) {
                List<Map<String, Object>> members = team.getMembers().stream().map(u -> {
                    Map<String, Object> mu = new HashMap<>();
                    mu.put("id", u.getId());
                    mu.put("email", u.getEmail());
                    return mu;
                }).collect(Collectors.toList());
                resp.put("members", members);
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<?> getTeamMembers(@PathVariable Long teamId) {
        try {
            Team team = teamService.getTeam(teamId);
            List<Map<String, Object>> members = team.getMembers().stream().map(u -> {
                Map<String, Object> mu = new HashMap<>();
                mu.put("id", u.getId());
                mu.put("email", u.getEmail());
                mu.put("nombre", u.getNombre());
                mu.put("apellidoPaterno", u.getApellidoPaterno());
                mu.put("apellidoMaterno", u.getApellidoMaterno());
                return mu;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<?> updateTeam(@PathVariable Long teamId, @RequestBody TeamDto teamDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Team team = teamService.updateTeam(teamId, email, teamDto);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", team.getId());
            resp.put("message", "Equipo actualizado");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<?> deleteTeam(@PathVariable Long teamId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            teamService.deleteTeam(teamId, email);
            return ResponseEntity.ok("Equipo eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{teamId}/join-requests")
    public ResponseEntity<?> createJoinRequest(@PathVariable Long teamId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            JoinRequest jr = teamService.createJoinRequest(teamId, email);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", jr.getId());
            resp.put("status", jr.getStatus());
            return ResponseEntity.status(201).body(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{teamId}/join-requests")
    public ResponseEntity<?> getJoinRequests(@PathVariable Long teamId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            List<JoinRequest> list = teamService.getJoinRequests(teamId, email);
            List<Map<String, Object>> resp = list.stream().map(j -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", j.getUser().getId());
                userMap.put("email", j.getUser().getEmail());
                userMap.put("nombre", j.getUser().getNombre());
                userMap.put("apellidoPaterno", j.getUser().getApellidoPaterno());
                userMap.put("apellidoMaterno", j.getUser().getApellidoMaterno());
                userMap.put("active", j.getUser().isActive());
                userMap.put("role", j.getUser().getRole().getName());

                Map<String, Object> m = new HashMap<>();
                m.put("id", j.getId());
                m.put("user", userMap);
                m.put("status", j.getStatus());
                m.put("createdAt", j.getCreatedAt());
                return m;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{teamId}/join-requests/{requestId}")
    public ResponseEntity<?> manageJoinRequest(@PathVariable Long teamId, @PathVariable Long requestId,
            @RequestBody ActionDto actionDto) {
        System.out.println("🔧 ENTRANDO a manageJoinRequest");
        System.out.println("🆔 TeamId: " + teamId + ", RequestId: " + requestId);
        System.out.println("⚡ Action: " + (actionDto != null ? actionDto.getAction() : "NULL"));

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            System.out.println("👤 Email del usuario: " + email);

            JoinRequest jr = teamService.manageJoinRequest(teamId, requestId, actionDto.getAction(), email);
            Map<String, Object> resp = new HashMap<>();
            resp.put("id", jr.getId());
            resp.put("status", jr.getStatus());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{teamId}/leave")
    public ResponseEntity<?> leaveTeam(@PathVariable Long teamId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            teamService.leaveTeam(teamId, email);
            return ResponseEntity.ok("Has abandonado el equipo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long teamId, @PathVariable Long userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            teamService.removeMember(teamId, userId, email);
            return ResponseEntity.ok("Miembro expulsado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
