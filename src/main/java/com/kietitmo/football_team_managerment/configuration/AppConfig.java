package com.kietitmo.football_team_managerment.configuration;

import com.kietitmo.football_team_managerment.entities.Role;
import com.kietitmo.football_team_managerment.entities.User;
import com.kietitmo.football_team_managerment.repositories.UserRepository;
import com.kietitmo.football_team_managerment.services.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppConfig {

    PasswordEncoder passwordEncoder;
    RoleService roleService;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if (userRepository.findByUsername("admin7").isEmpty()){
                var roles = new HashSet<Role>();
                var adminRole = roleService.getRole("ADMIN");
                roles.add(adminRole);

                User user = User.builder()
                        .username("admin7")
                        .password(passwordEncoder.encode("admin7"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}