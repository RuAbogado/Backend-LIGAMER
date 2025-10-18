package mx.edu.utez.ligamerbackend.services;

import mx.edu.utez.ligamerbackend.dtos.UserDto;
import mx.edu.utez.ligamerbackend.models.Role;
import mx.edu.utez.ligamerbackend.models.User;
import mx.edu.utez.ligamerbackend.repositories.RoleRepository;
import mx.edu.utez.ligamerbackend.repositories.UserRepository;
import mx.edu.utez.ligamerbackend.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public User registerNewUser(UserDto userDto) throws Exception {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setActive(true);

        Role userRole = roleRepository.findByName(AppConstants.ROLE_JUGADOR)
                .orElseThrow(() -> new Exception("Rol de Jugador no encontrado."));
        newUser.setRole(userRole);

        return userRepository.save(newUser);
    }

    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + email));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(expiryDate);
        userRepository.save(user);

        sendPasswordResetEmail(user.getEmail(), token);
    }

    private void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Restablecimiento de Contraseña - LIGAMER");
        String url = "http://localhost:3000/reset-password?token=" + token;
        message.setText("Hola,\n\nHas solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para continuar:\n" + url + "\n\nSi no solicitaste esto, por favor ignora este correo.\n\nEl enlace caducará en 15 minutos.");
        mailSender.send(message);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Token de restablecimiento inválido."));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace de restablecimiento ha caducado. Por favor, solicita uno nuevo.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + email));
    }

    @Transactional
    public User updateProfile(String currentEmail, String newEmail, String currentPassword, String newPassword) throws Exception {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (newEmail != null && !newEmail.equals(currentEmail)) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new Exception("El correo electrónico ya está en uso.");
            }
            user.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new Exception("La contraseña actual es incorrecta.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        return userRepository.save(user);
    }

    public void changePassword(String email, String currentPassword, String newPassword) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new Exception("La contraseña actual es incorrecta.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> listAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
    }

    @Transactional
    public User updateUserActive(Long id, Boolean active) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        if (active != null) user.setActive(active);
        return userRepository.save(user);
    }

    @Transactional
    public void assignOrganizerRole(Long userId, boolean assign) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        String targetRoleName = assign ? AppConstants.ROLE_ORGANIZADOR : AppConstants.ROLE_JUGADOR;
        Role role = roleRepository.findByName(targetRoleName).orElseThrow(() -> new Exception("Rol no encontrado: " + targetRoleName));
        user.setRole(role);
        userRepository.save(user);
    }
}
