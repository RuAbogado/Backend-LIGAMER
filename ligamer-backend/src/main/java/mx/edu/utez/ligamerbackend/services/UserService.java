package mx.edu.utez.ligamerbackend.services;

import mx.edu.utez.ligamerbackend.models.Role;
import mx.edu.utez.ligamerbackend.models.User;
import mx.edu.utez.ligamerbackend.repositories.RoleRepository;
import mx.edu.utez.ligamerbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(User user) throws Exception {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_JUGADOR")
                .orElseThrow(() -> new Exception("Rol de Jugador no encontrado."));
        user.setRole(userRole);

        return userRepository.save(user);
    }
}