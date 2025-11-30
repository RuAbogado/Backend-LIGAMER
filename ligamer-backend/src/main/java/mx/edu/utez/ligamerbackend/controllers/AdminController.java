package mx.edu.utez.ligamerbackend.controllers;

import jakarta.validation.Valid;
import mx.edu.utez.ligamerbackend.dtos.AdminUpdateUserDto;
import mx.edu.utez.ligamerbackend.dtos.AssignOrganizerDto;
import mx.edu.utez.ligamerbackend.models.User;
import mx.edu.utez.ligamerbackend.services.UserService;
import mx.edu.utez.ligamerbackend.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminController {

    @Autowired
    private mx.edu.utez.ligamerbackend.repositories.TeamRepository teamRepository;

    @Autowired
    private UserService userService;

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User requester = userService.findByEmail(email);
        return requester.getRole() != null && AppConstants.ROLE_ADMINISTRADOR.equals(requester.getRole().getName());
    }

    @GetMapping
    public ResponseEntity<?> listUsers() {
        try {
            if (!isAdmin())
                return ResponseEntity.status(403).body("No autorizado");

            List<User> users = userService.listAllUsers();
            List<mx.edu.utez.ligamerbackend.models.Team> allTeams = teamRepository.findAll();
            Map<Long, mx.edu.utez.ligamerbackend.models.Team> userTeamMap = new HashMap<>();

            for (mx.edu.utez.ligamerbackend.models.Team t : allTeams) {
                if (t.getMembers() != null) {
                    for (User u : t.getMembers()) {
                        userTeamMap.put(u.getId(), t);
                    }
                }
            }

            List<Map<String, Object>> resp = users.stream().map(u -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", u.getId());
                m.put("email", u.getEmail());
                m.put("nombre", u.getNombre());
                m.put("apellidoPaterno", u.getApellidoPaterno());
                m.put("apellidoMaterno", u.getApellidoMaterno());
                m.put("active", u.isActive());
                m.put("role", u.getRole() != null ? u.getRole().getName() : null);

                mx.edu.utez.ligamerbackend.models.Team t = userTeamMap.get(u.getId());
                if (t != null) {
                    m.put("teamName", t.getName());
                    m.put("teamMemberCount", t.getMembers() != null ? t.getMembers().size() : 0);
                } else {
                    m.put("teamName", null);
                    m.put("teamMemberCount", 0);
                }

                return m;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            if (!isAdmin())
                return ResponseEntity.status(403).body("No autorizado");
            User user = userService.getUserById(userId);
            Map<String, Object> m = new HashMap<>();
            m.put("id", user.getId());
            m.put("email", user.getEmail());
            m.put("active", user.isActive());
            m.put("role", user.getRole() != null ? user.getRole().getName() : null);
            return ResponseEntity.ok(m);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Valid @RequestBody AdminUpdateUserDto dto) {
        try {
            if (!isAdmin())
                return ResponseEntity.status(403).body("No autorizado");
            User updated = userService.updateUserActive(userId, dto.getActive());
            Map<String, Object> m = new HashMap<>();
            m.put("id", updated.getId());
            m.put("active", updated.isActive());
            return ResponseEntity.ok(m);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{userId}/assign-organizer")
    public ResponseEntity<?> assignOrganizer(@PathVariable Long userId, @Valid @RequestBody AssignOrganizerDto dto) {
        try {
            if (!isAdmin())
                return ResponseEntity.status(403).body("No autorizado");
            userService.assignOrganizerRole(userId, dto.getAssign());
            Map<String, Object> m = new HashMap<>();
            m.put("userId", userId);
            m.put("assign", dto.getAssign());
            return ResponseEntity.ok(m);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
