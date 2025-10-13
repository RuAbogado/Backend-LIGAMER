package mx.edu.utez.ligamerbackend.controllers;

import mx.edu.utez.ligamerbackend.dtos.UpdateProfileDto;
import mx.edu.utez.ligamerbackend.models.User;
import mx.edu.utez.ligamerbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getProfile() {
        try {
            // Obtener el usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            // Buscar el usuario en la base de datos
            User user = userService.findByEmail(email);

            // Preparar la respuesta con la información del perfil
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("email", user.getEmail());
            profile.put("active", user.isActive());
            profile.put("role", user.getRole().getName());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener el perfil: " + e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto updateProfileDto) {
        try {
            // Obtener el usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentEmail = authentication.getName();

            // Actualizar el perfil del usuario
            User updatedUser = userService.updateProfile(
                    currentEmail,
                    updateProfileDto.getEmail(),
                    updateProfileDto.getCurrentPassword(),
                    updateProfileDto.getNewPassword()
            );

            // Preparar la respuesta con la información actualizada
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Perfil actualizado exitosamente");
            response.put("user", Map.of(
                    "id", updatedUser.getId(),
                    "email", updatedUser.getEmail(),
                    "active", updatedUser.isActive(),
                    "role", updatedUser.getRole().getName()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el perfil: " + e.getMessage());
        }
    }
}
