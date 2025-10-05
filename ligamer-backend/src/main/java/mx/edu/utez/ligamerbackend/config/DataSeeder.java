package mx.edu.utez.ligamerbackend.config;

import mx.edu.utez.ligamerbackend.models.Role;
import mx.edu.utez.ligamerbackend.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createRoleIfNotFound("ROLE_JUGADOR");
        createRoleIfNotFound("ROLE_ORGANIZADOR");
        createRoleIfNotFound("ROLE_ADMINISTRADOR");
    }

    private void createRoleIfNotFound(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role newRole = new Role();
            newRole.setName(name);
            roleRepository.save(newRole);
        }
    }
}