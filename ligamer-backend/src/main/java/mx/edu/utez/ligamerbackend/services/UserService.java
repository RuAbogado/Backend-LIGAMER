package mx.edu.utez.ligamerbackend.services;

import mx.edu.utez.ligamerbackend.dtos.UserDto;
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

    @Transactional
    public User registerNewUser(UserDto userDto) throws Exception {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setActive(true);

        Role userRole = roleRepository.findByName("ROLE_JUGADOR")
                .orElseThrow(() -> new Exception("Rol de Jugador no encontrado."));
        newUser.setRole(userRole);

        return userRepository.save(newUser);
    }
}